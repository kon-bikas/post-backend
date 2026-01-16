package org.kon.postr.validation.file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class FileContentTypeValidator implements ConstraintValidator<ContentType, MultipartFile> {

    private Set<String> validContentTypes;

    @Override
    public void initialize(ContentType constraintAnnotation) {
        validContentTypes = Set.of(constraintAnnotation.types());
    }

    @Override
    public boolean isValid(MultipartFile multipartFile,
                           ConstraintValidatorContext constraintValidatorContext) {

        return (multipartFile == null) ||
                validContentTypes.contains(multipartFile.getContentType());
    }
}
