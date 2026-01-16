package org.kon.postr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Value(value = "${spring.profiles.active}")
    private String profile;

    @Value(value = "${app.storage.accessKey}")
    private String accessKey;

    @Value(value = "${app.storage.secretKey}")
    private String secretKey;

    @Value(value = "${app.storage.host_ip}")
    private String hostIp;

    @Value(value = "${app.storage.host_port}")
    private Integer hostPort;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder s3Builder = S3Client.builder()
                .region(Region.EU_WEST_3)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                );

        if (profile.equals("prod")) return s3Builder.build();

        return s3Builder
                .endpointOverride(URI.create("http://" + hostIp + ":" + hostPort))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        S3Presigner.Builder s3PresignerBuilder = S3Presigner.builder()
                .region(Region.EU_WEST_3)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                );

        if (profile.equals("prod")) return s3PresignerBuilder.build();

        return s3PresignerBuilder
                .endpointOverride(URI.create("http://" + hostIp + ":" + hostPort))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

}
