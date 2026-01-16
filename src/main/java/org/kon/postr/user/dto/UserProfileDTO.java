package org.kon.postr.user.dto;

import java.util.UUID;

public record UserProfileDTO(

        UUID id,

        String username,

        String picture,

        String description,

        Long followingCount,

        Long followersCount,

        Long postCount,

        boolean following

) {
}
