package com.codeit.sb06deokhugamteam2.book.storage;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.AWSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Component
public class S3Storage {
    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public S3Storage(
            @Value("${spring.cloud.aws.s3.bucket}")
            String bucket,
            @Value("${spring.cloud.aws.region.static}")
            String region,
            S3Client s3Client
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.region = region;
    }

    public void putThumbnail(String key, byte[] bytes, String contentType) {
        try {
            PutObjectRequest putObjectRequest =  PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            RequestBody requestBody = RequestBody.fromBytes(bytes);
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (S3Exception e) {
            Map<String, Object> details = Map.of(
                    "key", key
            );
            throw new AWSException(ErrorCode.AWS_EXCEPTION, details, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getThumbnail(String key) {
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }
}
