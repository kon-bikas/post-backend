package org.kon.postr.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kon.postr.keycloak.dto.KeycloakEventUser;
import org.kon.postr.security.JwtAuthenticationEntryPoint;
import org.kon.postr.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMQKeycloakEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQKeycloakEventConsumer.class);

    private final UserService userService;

    @Autowired
    public RabbitMQKeycloakEventConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeMessage(String message) throws JsonProcessingException {
        logger.info("got message event: {}", message);
        this.handleMessage(message);
    }

    /*
     * The json will have the following structure:
     * {
     *      "eventType": <event-type>,
     *      "userUUID": <user-keycloak-id>,
     *      "attributes": {
     *              "lastName":[<user-lastname>],
     *              "firstName":[<user-firstname>],
     *              "email":[<user-email>],
     *              "username":[<user-username>]
     *          }
     *      }
     *
     * attributes have their value in a list because keycloak's getAttributes method called in the provider
     * returns type Map<String, List<String>>
     */
    private void handleMessage(String message) throws JsonProcessingException {
        JsonNode json = new ObjectMapper().readTree(message);
        String eventType = json.get("eventType").textValue();
        String userId = json.get("userUUID").textValue();

        JsonNode attributes = json.get("attributes");
        String username = attributes.get("username").get(0).textValue();
        String email = attributes.get("email").get(0).textValue();

        KeycloakEventUser eventUser = new KeycloakEventUser(
                UUID.fromString(userId),
                username
        );

        userService.save(eventUser);

    }

}
