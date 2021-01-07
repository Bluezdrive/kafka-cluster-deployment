package de.volkerfaas.kafka.deployment.controller.model;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class OAuth2Token {

    private final String registrationId;
    private final String id;
    private final String name;
    private final String email;
    private final boolean authenticated;

    public OAuth2Token(OAuth2AuthenticationToken token) {
        this.registrationId = token.getAuthorizedClientRegistrationId();
        this.id = token.getName();
        this.name = token.getPrincipal().getAttribute("name");
        this.email = token.getPrincipal().getAttribute("email");
        this.authenticated = token.isAuthenticated();
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

}
