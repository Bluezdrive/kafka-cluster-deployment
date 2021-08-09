package de.volkerfaas.kafka.deployment.service.impl;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.config.TaskConfig;
import de.volkerfaas.kafka.deployment.controller.model.SkipablePageRequest;
import de.volkerfaas.kafka.deployment.controller.model.github.PushEvent;
import de.volkerfaas.kafka.deployment.integration.JobProducer;
import de.volkerfaas.kafka.deployment.integration.JobRepository;
import de.volkerfaas.kafka.deployment.model.Event;
import de.volkerfaas.kafka.deployment.model.Job;
import de.volkerfaas.kafka.deployment.model.Status;
import de.volkerfaas.kafka.deployment.model.Task;
import de.volkerfaas.kafka.deployment.service.*;
import de.volkerfaas.utils.MultiTaggedCounter;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static de.volkerfaas.utils.ExceptionUtils.handleException;

@Service
public class JobServiceImpl implements JobService, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    private final Config config;
    private final JavaMailSender javaMailSender;
    private final TaskService taskService;
    private final GitService gitService;
    private final JobRepository jobRepository;
    private final JobProducer jobProducer;
    private final BlockingQueue<Long> queue;

    private final Counter counterRepositoryChanged;
    private final Counter counterRepositoryPolled;
    private final MultiTaggedCounter counterJob;

    @Autowired
    public JobServiceImpl(Config config, JavaMailSender sender, TaskService taskService, GitService gitService, JobRepository jobRepository, JobProducer jobProducer) {
        this.config = config;
        this.javaMailSender = sender;
        this.gitService = gitService;
        this.taskService = taskService;
        this.jobRepository = jobRepository;
        this.jobProducer = jobProducer;
        this.queue = new LinkedBlockingQueue<>();

        Gauge.builder("kcds.job.queue.size", this.queue, Collection::size)
                .description("The number of jobs waiting to be executed")
                .register(Metrics.globalRegistry);
        Gauge.builder("kcds.jobs.total", this.jobRepository::count)
                .description("The number of jobs that haven been executed")
                .register(Metrics.globalRegistry);
        this.counterRepositoryChanged = Counter.builder("kcds.repository.changed")
                .description("The number of changes made to the repository")
                .register(Metrics.globalRegistry);
        this.counterRepositoryPolled = Counter.builder("kcds.repository.polled")
                .description("The number of times the repository has been polled")
                .register(Metrics.globalRegistry);
        this.counterJob = new MultiTaggedCounter("kcds.job", Metrics.globalRegistry, "status");
    }

    @PostConstruct
    void init() throws IOException {
        final Path workingDirectoryPath = Path.of(config.getWorkingDirectory());
        Files.createDirectories(workingDirectoryPath);
        Executors.newSingleThreadExecutor().execute(this);
        jobRepository.findNotFinished().forEach(jobId -> handleException(() -> {
            queue.put(jobId);
            LOGGER.info("Restored job into queue with id {}", jobId);
            return null;
        }));
    }

    @Override
    public long queueNewJob(PushEvent pushEvent) throws BadEventException, InterruptedException {
        final String repository = config.getGit().getRepository();
        final String eventRepository = pushEvent.getRepository().getFullName();
        if (!Objects.equals(repository, eventRepository)) {
            LOGGER.error("Invalid repository {}", eventRepository);
            throw new BadEventException("repository", eventRepository);
        }
        final String branch = config.getGit().getBranch();
        final String eventBranch = getBranchFromRef(pushEvent.getRef());
        if (!Objects.equals(branch, eventBranch)) {
            LOGGER.error("Invalid branch {}", eventBranch);
            throw new BadEventException("branch", eventBranch);
        }
        Event event = new Event(pushEvent);
        final long jobId = createJob(event, repository, branch);
        queue.put(jobId);
        LOGGER.info("Put job into queue with id {}", jobId);

        return jobId;
    }

    @Override
    public long restartJob(long jobId) throws BadEventException, InterruptedException {
        final Job job = jobRepository.findLatest().orElse(null);
        if (Objects.nonNull(job) && !Objects.equals(Status.FAILED, job.getStatus())) {
            throw new BadEventException("status of latest job", job.getStatus().toString());
        }
        Event event = new Event();
        event.setRepositoryPushedAt(new Date(System.currentTimeMillis() / 1000));
        final String repository = config.getGit().getRepository();
        final String branch = config.getGit().getBranch();
        final long restartedJobId = createJob(event, repository, branch, job);
        queue.put(restartedJobId);
        LOGGER.info("Put restarted job into queue with id {}", restartedJobId);

        return restartedJobId;
    }

    @Scheduled(cron = "${config.git.cron:-}")
    public void triggerNewJob() throws InterruptedException {
        final Job job = jobRepository.findLatest().orElse(null);
        final String repository = config.getGit().getRepository();
        final String branch = config.getGit().getBranch();
        if (gitService.isRepositoryChanged(repository, branch, job)) {
            counterRepositoryChanged.increment();

            Event event = new Event();
            event.setRepositoryPushedAt(new Date(System.currentTimeMillis() / 1000));
            final long jobId = createJob(event, repository, branch);
            queue.put(jobId);
            LOGGER.info("Put job into queue with id {}", jobId);
        }
        this.counterRepositoryPolled.increment();
    }

    @Override
    public Job findJobById(long id) throws NotFoundException {
        return jobRepository.findById(id).orElseThrow(() -> new NotFoundException(Job.class, id));
    }

    @Override
    public Page<Job> listJobs(SkipablePageRequest pageable) {
       return jobRepository.findAllBySkipablePageRequest(pageable);
    }

    @Override
    public void run() {
        do {
            try {
                final long jobId = queue.take();
                final Job job = startJob(jobId);
                    executeTasks(job);
                    finishJob(job);
            } catch (NotFoundException e) {
                LOGGER.error(e.getMessage());
                final long id = Long.parseLong(e.getId().toString());
                try {
                    finishJob(id);
                } catch (NotFoundException | MessagingException | UnsupportedEncodingException ignored) {
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupting with error", e);
                Thread.currentThread().interrupt();
                break;
            } catch (MessagingException e) {
                LOGGER.error("Error sending mail", e);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Error sending mail", e);
            }
        } while (true);
    }

    @Override
    public boolean isValidGitHubSignature(String signature, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(config.getGit().getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        final String value = "sha256=".concat(Hex.encodeHexString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8))));
        if (!Objects.equals(signature, value)) {
            LOGGER.error("Invalid signature {}", signature);
            return false;
        }

        return true;
    }

    @Transactional
    @Timed(value = "kcds.task.duration", description = "Time spent executing tasks")
    public void executeTasks(Job job) {
        for (Task task : job.getTasks()) {
            task.setStatus(Status.RUNNING);
            task.setStartTimeMillis(System.currentTimeMillis());
            save(job);
            try {
                final int exitCode = switch(task.getType()) {
                    case COMMAND_LINE -> taskService.executeCommandLineTask(task);
                    case GIT -> taskService.executeGitTask(task);
                };
                save(job);
                if (exitCode != 0) {
                    break;
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Task failed with exception: ", e);
            }
        }
    }

    @Transactional
    public long createJob(Event event, String repository, String branch) {
        return createJob(event, repository, branch, null);
    }

    @Transactional
    public long createJob(Event event, String repository, String branch, Job reference) {
        final Job job = new Job(event, repository, branch);
        final Map<String, TaskConfig> taskConfigs = config.getTasks();
        final Set<Task> tasks = taskConfigs.entrySet().stream()
                .sorted(Comparator.comparingInt(a -> a.getValue().getOrder()))
                .map(entry -> taskService.create(job, entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Task::getOrderId))));
        job.setTasks(tasks);
        job.setReference(reference);
        final Job createdJob = save(job);
        LOGGER.info("Created job {}", createdJob.getId());

        return createdJob.getId();
    }

    @Transactional
    public void finishJob(long jobId) throws NotFoundException, MessagingException, UnsupportedEncodingException {
        final Job job = jobRepository.findById(jobId).orElseThrow(() -> new NotFoundException(Job.class, jobId));
        finishJob(job);
    }

    @Transactional
    public void finishJob(Job job) throws MessagingException, UnsupportedEncodingException {
        final boolean allTasksSuccessful = job.getTasks().stream()
                .map(Task::getStatus)
                .map(status -> status == Status.SUCCESS)
                .reduce(true, (previousTaskStatus, currentTaskStatus) -> previousTaskStatus && currentTaskStatus);
        job.setStatus(allTasksSuccessful ? Status.SUCCESS : Status.FAILED);
        job.setEndTimeMillis(System.currentTimeMillis());
        counterJob.increment(job.getStatus().name());
        LOGGER.info("Finished job {}", job.getId());
        sendMail(save(job));
    }

    @Transactional
    public Job startJob(long jobId) throws NotFoundException {
        final Job job = jobRepository.findById(jobId).orElseThrow(() -> new NotFoundException(Job.class, jobId));
        job.setStatus(Status.RUNNING);
        job.setStartTimeMillis(System.currentTimeMillis());
        LOGGER.info("Started job {}", jobId);

        return save(job);
    }

    private Job save(Job job) {
        return jobProducer.send(jobRepository.saveAndFlush(job));
    }

    private void sendMail(Job job) throws MessagingException, UnsupportedEncodingException {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        final String text = "Kafka cluster deployment job #" + job.getId() + " finished " + (job.getStatus() == Status.SUCCESS ? "successfully" : "with errors");
        helper.setFrom(config.getMail().getFrom(), config.getMail().getPersonal());
        helper.setTo(config.getMail().getTo());
        helper.setSubject(text);
        helper.setText("<p>" + text + "</p>", true);
        javaMailSender.send(mimeMessage);
    }

    private String getBranchFromRef(String ref) {
        if (Objects.isNull(ref)) {
            return null;
        }
        final String[] items = ref.split("/");
        if (items.length < 3) {
            return null;
        }
        return items[2];
    }

}
