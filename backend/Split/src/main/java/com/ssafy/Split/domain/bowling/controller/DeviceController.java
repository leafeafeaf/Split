package com.ssafy.Split.domain.bowling.controller;


import com.ssafy.Split.domain.bowling.domain.dto.request.DeviceMeasurementRequest;
import com.ssafy.Split.domain.bowling.domain.dto.request.FrameUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.request.VideoUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.response.FrameUploadResponse;
import com.ssafy.Split.domain.bowling.domain.dto.response.VideoUploadResponse;
import com.ssafy.Split.domain.bowling.service.DeviceService;
import com.ssafy.Split.domain.bowling.service.FrameService;
import com.ssafy.Split.global.common.exception.ErrorResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
@Slf4j
public class DeviceController {

  private final DeviceService deviceService;
  private final FrameService frameService;

  /**
   * 디바이스 측정 시작
   **/
  @PostMapping("/{serial}")
  public ResponseEntity<ErrorResponse> startMeasurement(
      @PathVariable Integer serial,
      @Valid @RequestBody DeviceMeasurementRequest request
  ) {
    log.info("Starting measurement for device: {}", serial);
    deviceService.startMeasurement(serial, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ErrorResponse.builder()
            .code("SUCCESS")
            .status(201)
            .message("Measurement started successfully")
            .timestamp(LocalDateTime.now().toString())
            .build());

  }

  @PostMapping("/{serial}/frame")
  public ResponseEntity<FrameUploadResponse> uploadFrame(
      @PathVariable Integer serial,
      @Valid @RequestBody FrameUploadRequest request) {

    Integer frameNum = frameService.uploadFrame(serial, request);

    FrameUploadResponse response = FrameUploadResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Frames retrieved successfully")
        .timestamp(LocalDateTime.now().toString())
        .data(FrameUploadResponse.FrameData.builder()
            .num(frameNum)
            .build())
        .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{serial}/frame/{frameNum}/video")
  public ResponseEntity<?> uploadVideo(
      @PathVariable Integer serial,
      @PathVariable Integer frameNum,
      @Valid @RequestBody VideoUploadRequest request) {

    frameService.uploadVideo(serial, frameNum, request);

    return ResponseEntity.ok(VideoUploadResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("video upload successfully")
        .timestamp(LocalDateTime.now().toString())
        .build());
  }


}