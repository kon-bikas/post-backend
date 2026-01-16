package org.kon.postr.storage;

import org.kon.postr.exception.ObjectStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
public class S3Service implements ObjectStorageService {

    @Value(value = "${app.storage.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Autowired
    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public void upload(String keyName, byte[] bytes, String contentType) {
        try {
            s3Client.putObject(
                    b -> b.bucket(bucketName).key(keyName).contentType(contentType),
                    RequestBody.fromBytes(bytes)
            );
        } catch (Exception e) {
            throw new ObjectStorageException("Error while uploading image", e);
        }
    }

    @Override
    public String getPresignedUrl(String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest getObjectPresignedRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(10))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                    getObjectPresignedRequest
            );

            return presignedRequest.url().toExternalForm();

        } catch (Exception e) {
            throw new ObjectStorageException("Error while getting presigned url", e);
        }
    }

}
