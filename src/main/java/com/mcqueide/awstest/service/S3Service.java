package com.mcqueide.awstest.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3Service {
    
    // Add a log object using sl4j
    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    public List<String> listBuckets() {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build()) {
            return s3Client.listBuckets().buckets().stream()
                    .map(bucket -> bucket.name())
                    .toList();
        }
    }

    public List<String> getBucketObjects(String bucketName) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build()) {
            var listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

            var listRes = s3Client.listObjectsV2Paginator(listReq);
            return listRes.stream()
                .flatMap(r->r.contents().stream())
                .map(obj -> obj.key())
                .toList();
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] getFileContent(String bucketName, String objectKey) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build()) {
            var getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

            try (var s3Object = s3Client.getObject(getObjectRequest)) {
                return s3Object.readAllBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting file content from S3", e);
        }
    }

    public void uploadFile(String bucketName, String objectKey, byte[] content) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build()) {
            var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    public void deleteObject(String bucketName, String objectKey) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build()) {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from S3", e);
        }
    }
}
