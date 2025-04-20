package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    // Inject AWS bucket name from application.properties
    @Value("${aws.bucket.name}")
    private String bucketName;

    // Inject the S3 client for interacting with Amazon S3
    private final S3Client s3Client;

    /**
     * Uploads a file to Amazon S3 bucket and returns the file URL.
     * @param file The file to be uploaded.
     * @return The file's URL in the S3 bucket.
     */
    @Override
    public String uploadFile(MultipartFile file) {
        // Extract the file extension
        String fileNameExtension =  file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);

        // Generate a unique file key (name) using UUID
        String key = UUID.randomUUID().toString() + "." + fileNameExtension;

        try {
            // Prepare the PutObjectRequest to upload the file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // Specify the S3 bucket name
                    .key(key) // Set the unique file key
                    .acl("public-read") // Set the file's ACL to public-read (accessible by anyone)
                    .contentType(file.getContentType()) // Set the content type of the file
                    .build();

            // Upload the file to S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // If the upload was successful, return the file URL
            if (response.sdkHttpResponse().isSuccessful()){
                return "https://" + bucketName + ".s3.amazonaws.com/" + key;
            } else {
                // If there was an error in uploading, throw an exception
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");
            }
        } catch (IOException e) {
            // Handle any IOExceptions during file upload
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");
        }
    }

    /**
     * Deletes a file from Amazon S3 using its URL.
     * @param imgUrl The URL of the file to be deleted.
     * @return True if the file was deleted successfully.
     */
    @Override
    public boolean deleteFile(String imgUrl) {
        // Extract the file name (key) from the URL
        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
//
//        // Prepare the DeleteObjectRequest to delete the file from S3
//        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(bucketName) // Specify the S3 bucket name
//                .key(fileName) // Set the file key (file name)
//                .build();
//
//        // Delete the file from S3
//        s3Client.deleteObject(deleteObjectRequest);
//
//        // Return true to indicate the file was deleted
//        return true;


        try {
            // Prepare the DeleteObjectRequest to delete the file from S3
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName) // Specify the S3 bucket name
                    .key(fileName) // Set the file key (file name)
                    .build();

            // Delete the file from S3
            s3Client.deleteObject(deleteObjectRequest);
            return true; // Successfully deleted the file
        } catch (Exception e) {
            // Handle any errors and log them
            System.err.println("Error deleting file from S3: " + e.getMessage());
            return false; // Return false if deletion fails
        }

    }
}
