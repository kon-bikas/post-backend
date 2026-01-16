package org.kon.postr.user.dto;

import java.util.UUID;

public record UserCardDTO(

        UUID id,

        String username,

        String picture
) {
}
