package com.ssafy.Split.domain.game.controller;


import com.ssafy.Split.domain.game.domain.dto.response.GameListResponse;
import com.ssafy.Split.domain.game.domain.dto.response.GameResponse;
import com.ssafy.Split.domain.game.service.GameService;
import com.ssafy.Split.domain.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.domain.game.domain.dto.response.GameUploadResponse;
import com.ssafy.Split.domain.user.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    /** 게임 id로 게임 조회 **/
        @GetMapping("/{gameId}")
        public ResponseEntity<GameResponse> getGame(@PathVariable Integer gameId) {
            GameResponse response = gameService.getGame(gameId);
            return ResponseEntity.ok(response);
        }

    /**  유저 id를 통한 게임 조회
     *   차후 토큰으로 수정 필요
     *
     * **/

    @GetMapping
    public ResponseEntity<GameListResponse> getGameList(
            @RequestHeader("Authorization") String userId,
            @RequestParam(required = false) Integer count) {

        GameListResponse response = gameService.getGameList(
                Integer.parseInt(userId),
                count
        );
        return ResponseEntity.ok(response);
    }
}


