package de.volkerfaas.kafka.deployment.controller;

import de.volkerfaas.kafka.deployment.controller.model.ErrorResponse;
import de.volkerfaas.kafka.deployment.model.GitStatus;
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

@RestController
public class GitStatusApiController {

    private final GitService gitService;

    @Autowired
    public GitStatusApiController(GitService gitService) {
        this.gitService = gitService;
    }

    @GetMapping(path = "/api/gitStatus/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GitStatus findLatest() throws NotFoundException {
        return gitService.findLatest();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(HttpServletRequest request, Exception e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getServletPath());
    }

}
