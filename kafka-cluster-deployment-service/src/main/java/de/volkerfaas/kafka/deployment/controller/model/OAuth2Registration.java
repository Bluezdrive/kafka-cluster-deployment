package de.volkerfaas.kafka.deployment.controller.model;

import org.springframework.security.oauth2.client.registration.ClientRegistration;

public class OAuth2Registration {

    private final String clientName;
    private final String registrationId;

    public OAuth2Registration(ClientRegistration clientRegistration) {
        this.clientName = clientRegistration.getClientName();
        this.registrationId = clientRegistration.getRegistrationId();
    }

    public String getClientName() {
        return clientName;
    }

    public String getRegistrationId() {
        return registrationId;
    }

}
