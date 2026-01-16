package org.kon.postr.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.kon.postr.validation.file.ContentType;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserDTO(

        @ContentType(
                types = { "image/png", "image/jpeg" },
                message = "File should be of type png or jpeg"
        )
        MultipartFile picture,

        @NotBlank(message = "content should not be empty")
        @Size(
                min = 3,
                max = 300,
                message = "username should be 3 to 300 characters long.")
        String username,

        @NotBlank(message = "content should not be empty")
        @Size(
                min = 3,
                max = 300,
                message = "first name should be 3 to 300 characters long.")
        String firstName,

        @NotBlank(message = "content should not be empty")
        @Size(
                min = 3,
                max = 300,
                message = "last name should be 3 to 300 characters long.")
        String lastName,

        @Pattern(
                regexp = "(^$|[0-9]{10})",
                message = "Not a valid phone number."
        )
        String phoneNumber

) {
}
