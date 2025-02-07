package com.ssafy.Split.game.controller;


import com.ssafy.Split.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.game.domain.dto.response.GameUploadResponse;
import com.ssafy.Split.game.service.GameService;
import com.ssafy.Split.user.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
@Slf4j
public class GameController {

    private final GameService gameService;


    // user 대한 정보는 jwt 구현을 통해 수정 예정
    @PostMapping
    public ResponseEntity<GameUploadResponse> uploadGame(
            @Valid @RequestBody GameUploadRequest request) {
        try {
            Integer gameId = gameService.uploadGame(request);

            return ResponseEntity.ok(GameUploadResponse.builder()
                    .code("SUCCESS")
                    .status(200)
                    .message("GAME upload successfully")
                    .timestamp(LocalDateTime.now().toString())
                    .data(GameUploadResponse.GameData.builder()
                            .id(gameId)
                            .build())
                    .build());

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GameUploadResponse.builder()
                            .code("ERROR")
                            .status(404)
                            .message(e.getMessage())
                            .timestamp(LocalDateTime.now().toString())
                            .build());
        }
    }
}

