package com.ssafy.Split.domain.bowling.controller;


import com.ssafy.Split.domain.bowling.domain.dto.request.DeviceMeasurementRequest;
import com.ssafy.Split.domain.bowling.service.DeviceService;
import com.ssafy.Split.domain.bowling.service.FrameService;
import com.ssafy.Split.global.common.response.ApiResponse;
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
  //TODO 등록시에 현재 serial 넘버가 존재하면 측정시작이 안되게 예외처리 필요
  @PostMapping("/{serial}")
  public ResponseEntity<ApiResponse> startMeasurement(
      @PathVariable Integer serial,
      @Valid @RequestBody DeviceMeasurementRequest request
  ) {
    log.info("Starting measurement for device: {}", serial);
    deviceService.startMeasurement(serial, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .code("SUCCESS")
            .status(201)
            .message("Measurement started successfully")
            .timestamp(LocalDateTime.now().toString())
            .build());
  }
}