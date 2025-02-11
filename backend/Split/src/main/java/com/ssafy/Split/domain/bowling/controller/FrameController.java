package com.ssafy.Split.domain.bowling.controller;


import static com.ssafy.Split.domain.bowling.domain.dto.response.FrameListResponse.FrameData;

import com.ssafy.Split.domain.bowling.domain.dto.request.FrameUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.request.VideoUploadRequest;
import com.ssafy.Split.domain.bowling.domain.dto.response.FrameListResponse;
import com.ssafy.Split.domain.bowling.domain.dto.response.FrameResponse;
import com.ssafy.Split.domain.bowling.domain.dto.response.FrameUploadResponse;
import com.ssafy.Split.domain.bowling.domain.dto.response.VideoUploadResponse;
import com.ssafy.Split.domain.bowling.domain.entity.Frame;
import com.ssafy.Split.domain.bowling.service.FrameService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
@Slf4j
public class FrameController {

  private final FrameService frameService;

  /**
   * 프레임 조회
   **/
  @GetMapping("/{serialNumber}/frame/{frameNum}")
  public ResponseEntity<FrameResponse> getFrame(
      @PathVariable Integer serialNumber,
      @PathVariable Integer frameNum) {

    Frame frame = frameService.getFrame(serialNumber, frameNum);

    FrameResponse.FrameData frameData = FrameResponse.FrameData.builder()
        .id(frame.getId())
        .progressId(frame.getProgress().getId())
        .serialNumber(frame.getDevice().getSerialNumber())
        .num(frame.getNum())
        .video(frame.getVideo())
        .isSkip(frame.getIsSkip() ? 1 : 0)
        .feedback(frame.getFeedback())
        .poseScore(frame.getPoseSocre())
        .elbowAngleScore(frame.getElbowAngleScore())
        .armStabilityScore(frame.getArmStabilityScore())
        .speed(frame.getSpeed())
        .build();

    return ResponseEntity.ok(FrameResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Frame data retrieved successfully")
        .data(frameData)
        .timestamp(LocalDateTime.now().toString())
        .build());

  }

  /**
   * 현재 기기에 프레임 전체 조회
   **/
  @GetMapping("/{serialNumber}/frame")
  public ResponseEntity<FrameListResponse> getAllFrames(
      @PathVariable Integer serialNumber
  ) {
    List<FrameData> frames = frameService.getAllFrames(serialNumber);

    return ResponseEntity.ok(FrameListResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Frame data retrieved successfully")
        .data(frames)
        .timestamp(LocalDateTime.now().toString())
        .build());
  }

  /**
   * 프레임 업로드
   **/
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


  /**
   * 프레임 비디오 업로드
   **/
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
