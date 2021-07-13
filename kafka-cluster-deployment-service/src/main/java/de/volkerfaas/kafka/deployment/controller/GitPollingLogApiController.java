package de.volkerfaas.kafka.deployment.controller;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.controller.model.ErrorResponse;
import de.volkerfaas.kafka.deployment.controller.model.ScheduleResponse;
import de.volkerfaas.kafka.deployment.model.GitPollingLog;
import de.volkerfaas.kafka.deployment.service.GitService;
import de.volkerfaas.kafka.deployment.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
public class GitPollingLogApiController {

    private final Config config;
    private final GitService gitService;

    @Autowired
    public GitPollingLogApiController(Config config, GitService gitService) {
        this.config = config;
        this.gitService = gitService;
    }

    @GetMapping(path = "/api/gitPollingLogs/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleResponse schedule()  throws NotFoundException {
        final String schedule = config.getGit().getCron();
        if (Objects.isNull(schedule) || schedule.isBlank()) {
            throw new NotFoundException("No schedule found");
        }
        return new ScheduleResponse(schedule);
    }

    @GetMapping(path = "/api/gitPollingLogs/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GitPollingLog findLatest() throws NotFoundException {
        return gitService.findLatest();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(HttpServletRequest request, Exception e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getServletPath());
    }

}
