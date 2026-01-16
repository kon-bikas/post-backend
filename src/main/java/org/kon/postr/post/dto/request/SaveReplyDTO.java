package org.kon.postr.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.kon.postr.validation.file.ContentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record SaveReplyDTO(

        @NotBlank(message = "content should not be empty")
        @Size(
                min = 3,
                max = 300,
                message = "content should be 3 to 300 characters long.")
        String content,

        @ContentType(
                types = { "image/png", "image/jpeg" },
                message = "File should be of type png or jpeg"
        )
        MultipartFile media,

//        @NotBlank
//        String userId,

        @NotBlank
        String parentId

) {
}
