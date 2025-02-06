package com.ssafy.Split.bowling.controller;


import com.ssafy.Split.bowling.domain.dto.request.DeviceMeasurementRequest;
import com.ssafy.Split.bowling.exception.DeviceInUseException;
import com.ssafy.Split.bowling.exception.DeviceNotFoundException;
import com.ssafy.Split.bowling.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/{serial}")
    public ResponseEntity<?> startMeasurement(
            @PathVariable String serial,
            @RequestBody DeviceMeasurementRequest request
    ) {
        try {
            deviceService.startMeasurement(serial, request, null);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (DeviceNotFoundException e) {
            log.error("Device not found: {}", serial);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (DeviceInUseException e) {
            log.error("Device in use: {}", serial);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }
}
