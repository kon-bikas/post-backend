package org.kon.postr.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value(value = "${app.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value(value = "${app.keycloak.realm}")
    private String keycloakRealm;

    @Value(value = "${app.keycloak.client-id}")
    private String keycloakClientId;

    @Value(value = "${app.keycloak.client-secret}")
    private String keycloakClientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(keycloakRealm)
                .clientId(keycloakClientId)
                .clientSecret(keycloakClientSecret)
                .build();
    }

}
