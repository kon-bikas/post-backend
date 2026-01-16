package org.kon.postr.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kon.postr.user.dto.UserCardDTO;

import java.sql.Timestamp;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostCardDTO(

        UUID id,

        PostCardDTO replyParent,

        UserCardDTO user,

        Timestamp createdAt,

        String content,

        String media,

        Long likeCount,

        Long replyCount,

        boolean hasLiked,

        boolean hasReposted,

        boolean isMine

) {
}
