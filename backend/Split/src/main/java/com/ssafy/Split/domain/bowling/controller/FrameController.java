package com.ssafy.Split.domain.bowling.controller;


import com.ssafy.Split.domain.bowling.domain.dto.response.FrameListResponse;
import com.ssafy.Split.domain.bowling.domain.dto.response.FrameResponse;
import com.ssafy.Split.domain.bowling.domain.entity.Frame;
import com.ssafy.Split.domain.bowling.service.FrameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.ssafy.Split.domain.bowling.domain.dto.response.FrameListResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
@Slf4j
public class FrameController {

    private final FrameService frameService;

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

    @GetMapping("/{serialNumber}/frames")
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

}
