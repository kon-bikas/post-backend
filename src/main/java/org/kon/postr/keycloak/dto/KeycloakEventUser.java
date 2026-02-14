package org.kon.postr.keycloak.dto;

import java.util.UUID;

public record KeycloakEventUser(

        UUID userId,

        String username

) {
}
