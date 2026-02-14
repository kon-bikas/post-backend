package org.kon.postr.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.kon.postr.exception.ResourceNotFoundException;
import org.kon.postr.user.dto.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakService {

    @Value(value = "${app.keycloak.realm}")
    private String keycloakRealm;

    private final Keycloak keycloak;

    @Autowired
    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public UserInfoDTO getUserInfo(String username) {
        List<UserRepresentation> userRepresentations = keycloak
                .realm(keycloakRealm)
                .users()
                .search(username, true);

        if (userRepresentations.isEmpty()) {
            throw new ResourceNotFoundException("keycloak user with name " + username + " not found");
        }

        UserRepresentation user = userRepresentations.get(0);

        return new UserInfoDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.firstAttribute("phone_number")
        );

    }

}
