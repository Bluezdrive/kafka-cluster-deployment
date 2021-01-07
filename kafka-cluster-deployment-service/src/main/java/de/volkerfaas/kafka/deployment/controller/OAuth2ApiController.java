package de.volkerfaas.kafka.deployment.controller;

import de.volkerfaas.kafka.deployment.controller.model.OAuth2Registration;
import de.volkerfaas.kafka.deployment.controller.model.OAuth2Token;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class OAuth2ApiController {

    private final InMemoryClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public OAuth2ApiController(InMemoryClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Operation(summary = "Get the OAuth2 token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the OAuth2 token", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OAuth2Token.class))
            })
    })
    @GetMapping(path = "/api/oauth2/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OAuth2Token token(OAuth2AuthenticationToken token) {
        return new OAuth2Token(token);
    }

    @Operation(summary = "Get all OAuth2 registrations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found OAuth2 registrations", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OAuth2Registration.class)))
            })
    })
    @GetMapping(path = "/api/oauth2/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OAuth2Registration> registrations() {
        final Iterator<ClientRegistration> iterator = clientRegistrationRepository.iterator();
        final Spliterator<ClientRegistration> spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);

        return StreamSupport.stream(spliterator, false)
                .map(OAuth2Registration::new)
                .collect(Collectors.toUnmodifiableList());
    }

}
