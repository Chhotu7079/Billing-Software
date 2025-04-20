package com.chhotu.billing_software.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


/**
 * This class configures the AWS S3 client for use in the application.
 */
@Configuration
public class AWSConfig {

    // Injects AWS access key from application properties
    @Value("${aws.access.key}")
    private String accessKey;

    // Injects AWS secret key from application properties
    @Value("${aws.secret.key}")
    private String secretKey;

    // Injects AWS region from application properties
    @Value("${aws.region}")
    private String region;


    /**
     * Defines a Spring bean for the AWS S3 client.
     * The client is configured with static credentials and a specified region.
     *
     * @return an S3Client instance
     */
    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .region(Region.of(region)) // Sets the AWS region
                // Provides static credentials for authentication
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();              // Builds the S3 client

    }

}
