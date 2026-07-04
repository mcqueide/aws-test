package com.mcqueide.awstest.rest;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcqueide.awstest.service.S3Service;

@RestController
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/buckets")
    public List<String> listBuckets() {
        return s3Service.listBuckets();
    }

    @GetMapping("/buckets/{bucketName}/objects")
    public List<String> getBucketObjects(@PathVariable String bucketName) {
        return s3Service.getBucketObjects(bucketName);
    }

    @GetMapping("/buckets/{bucketName}/objects/{objectKey}/content")
    public byte[] getFileContent(@PathVariable String bucketName, @PathVariable String objectKey) {
        return s3Service.getFileContent(bucketName, objectKey);
    }

    @PostMapping("/buckets/{bucketName}/objects/{objectKey}/content")
    public void uploadFile(@PathVariable String bucketName, @PathVariable String objectKey, 
            @RequestBody byte[] content) {
        s3Service.uploadFile(bucketName, objectKey, content);
    }

    @DeleteMapping("/buckets/{bucketName}/objects/{objectKey}")
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectKey) {
        s3Service.deleteObject(bucketName, objectKey);
    }

}
