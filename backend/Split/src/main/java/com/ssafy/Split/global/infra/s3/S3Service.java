package com.ssafy.Split.global.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public void deleteFile(String fileUrl) {
        try {
            // URL에서 키(파일 경로) 추출
            String key = extractKeyFromUrl(fileUrl);

            // S3에서 파일 삭제
            amazonS3.deleteObject(bucket, key);
            log.info("File deleted successfully from S3: {}", key);

        } catch (AmazonS3Exception e) {
            log.error("Error deleting file from S3: {}", fileUrl, e);
            throw new SplitException(ErrorCode.S3_DELETE_ERROR);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        // S3 URL에서 키 추출 (예: https://bucket-name.s3.region.amazonaws.com/folder/file.mp4)
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            // 첫 번째 '/'를 제거하여 키 추출
            return path.substring(1);
        } catch (MalformedURLException e) {
            throw new SplitException(ErrorCode.INVALID_FILE_URL);
        }
    }
}