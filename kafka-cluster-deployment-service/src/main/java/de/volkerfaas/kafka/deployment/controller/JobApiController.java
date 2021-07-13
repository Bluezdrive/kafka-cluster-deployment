package de.volkerfaas.kafka.deployment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.volkerfaas.kafka.deployment.controller.model.ErrorResponse;
import de.volkerfaas.kafka.deployment.controller.model.JobResponse;
import de.volkerfaas.kafka.deployment.controller.model.SkipablePageRequest;
import de.volkerfaas.kafka.deployment.controller.model.github.PushEvent;
import de.volkerfaas.kafka.deployment.model.Job;
import de.volkerfaas.kafka.deployment.service.BadEventException;
import de.volkerfaas.kafka.deployment.service.JobService;
import de.volkerfaas.kafka.deployment.service.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class JobApiController {

    private final JobService jobService;
    private final ObjectMapper objectMapper;

    @Autowired
    public JobApiController(JobService jobService, ObjectMapper objectMapper) {
        this.jobService = jobService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Create a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the job", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JobResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping(path = "/api/jobs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public JobResponse create(@RequestHeader("X-Hub-Signature-256") String signature, @RequestBody String payload) throws BadEventException, InterruptedException, JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException {
        if (!jobService.isValidGitHubSignature(signature, payload)) {
            throw new BadEventException("signature", signature);
        }
        final PushEvent event = objectMapper.readValue(payload, PushEvent.class);
        final long jobId = jobService.queueNewJob(event);

        return new JobResponse(jobId);
    }

    @Operation(summary = "Restart a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restarted the job", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JobResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PutMapping(path = "/api/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobResponse restart(@PathVariable long id) throws BadEventException, InterruptedException {
        final long jobId = jobService.restartJob(id);

        return new JobResponse(jobId);
    }

    @Operation(summary = "Get all jobs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found jobs", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Job.class)))
            })
    })
    @GetMapping(path = "/api/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<Job> list(SkipablePageRequest pageable) {
        return jobService.listJobs(pageable);
    }

    @Operation(summary = "Get a job by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the job", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
            }),
            @ApiResponse(responseCode = "404", description = "Job not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @GetMapping(path = "/api/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Job findById(@PathVariable long id) throws NotFoundException {
        return jobService.findJobById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(BadEventException.class)
    public ErrorResponse handleBadRequest(HttpServletRequest request, Exception e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request.getServletPath());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(HttpServletRequest request, Exception e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getServletPath());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleInternalServerError(HttpServletRequest request, Exception e) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request.getServletPath());
    }

}
