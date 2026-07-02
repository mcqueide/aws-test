package com.mcqueide.awstest.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class S3Service {
    
    public List<String> listBuckets() {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build()) {
            return s3Client.listBuckets().buckets().stream()
                    .map(bucket -> bucket.name())
                    .toList();
        }
    }
}
