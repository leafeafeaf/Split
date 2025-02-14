package com.ssafy.Split.global.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey;

  @Value("${cloud.aws.s3.region}")
  private String region;


  public void deleteFile(String fileUrl) {
    try {
      // URL에서 키(파일 경로) 추출
      String key = extractKeyFromUrl(fileUrl);

      // 객체 존재 여부 확인
      if (!amazonS3.doesObjectExist(bucket, key)) {
        throw new SplitException(ErrorCode.S3_FILE_NOT_FOUND);
      }
      // S3에서 파일 삭제
      amazonS3.deleteObject(bucket, key);
      log.info("File deleted successfully from S3: {}", key);

    } catch (AmazonS3Exception e) {
      log.error("Error deleting file from S3: {}", fileUrl, e);
      throw new SplitException(ErrorCode.S3_DELETE_ERROR);
    }
  }

  public void removeExpiration(String fileUrl) {
    // fileUrl에서 S3 키 추출
    String fileKey = extractKeyFromUrl(fileUrl);

    // 기존 객체의 메타데이터 가져오기
    ObjectMetadata metadata = amazonS3.getObjectMetadata(bucket, fileKey);

    // 기존 메타데이터에서 Expiration 제거
    metadata.setExpirationTime(null); // 유효기간 제거

    // 새로운 메타데이터로 덮어쓰기 (S3에서는 직접 변경이 안되므로 Copy 작업 수행)
    CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, fileKey, bucket, fileKey)
        .withNewObjectMetadata(metadata);

    amazonS3.copyObject(copyObjectRequest);
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

  public void updateObjectTag(String URL, String newExpireTag) {
    ObjectTagging newTagging = new ObjectTagging(Arrays.asList(new Tag("expire", newExpireTag)));

    String s3Key = extractKeyFromUrl(URL);

    // 객체 존재 여부 확인
    if (!amazonS3.doesObjectExist(bucket, s3Key)) {
      throw new SplitException(ErrorCode.S3_FILE_NOT_FOUND);
    }

    SetObjectTaggingRequest setTaggingRequest = new SetObjectTaggingRequest(bucket, s3Key,
        newTagging);
    amazonS3.setObjectTagging(setTaggingRequest);
    log.info("태그 변경 완료: {} | 새 유효기간: {}", s3Key, newExpireTag);
  }

}