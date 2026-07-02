package com.mcqueide.awstest.interfaces.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcqueide.awstest.application.service.S3Service;

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
}
