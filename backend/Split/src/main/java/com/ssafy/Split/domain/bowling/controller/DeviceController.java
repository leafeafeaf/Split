package com.ssafy.Split.domain.bowling.controller;


import com.ssafy.Split.domain.bowling.service.DeviceService;
import com.ssafy.Split.global.common.response.ApiResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
@Slf4j
public class DeviceController {

  private final DeviceService deviceService;

  /**
   * 디바이스 측정 시작
   **/
  @PostMapping("/{serial}")
  public ResponseEntity<ApiResponse> startMeasurement(
      @PathVariable Integer serial) {
    deviceService.startMeasurement(serial);
    log.info("디바이스 측정 시작 - serial : {}", serial);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .code("SUCCESS")
            .status(201)
            .message("Measurement started successfully")
            .timestamp(LocalDateTime.now().toString())
            .build());
  }
}