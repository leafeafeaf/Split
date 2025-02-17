package com.ssafy.Split.domain.game.controller;


import com.ssafy.Split.domain.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.domain.game.domain.dto.response.GameListResponse;
import com.ssafy.Split.domain.game.domain.dto.response.GameResponse;
import com.ssafy.Split.domain.game.domain.dto.response.GameUploadResponse;
import com.ssafy.Split.domain.game.service.GameService;
import com.ssafy.Split.global.common.JWT.domain.CustomUserDetails;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
@Slf4j
public class GameController {

  private final GameService gameService;

  /**
   * 게임 등록
   **/
  @PostMapping
  public ResponseEntity<GameUploadResponse> uploadGame(
      @Valid @RequestBody GameUploadRequest request) {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();

    Integer gameId = gameService.uploadGame(userId, request);
    log.info("게임 등록 - userId : {}, gameId : {}", userId, gameId);

    return ResponseEntity.ok(GameUploadResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("GAME upload successfully")
        .timestamp(LocalDateTime.now().toString())
        .data(GameUploadResponse.GameData.builder().id(gameId).build())
        .build());
  }

  /**
   * 게임 id로 게임 조회
   **/
  @GetMapping("/{gameId}")
  public ResponseEntity<GameResponse> getGame(@PathVariable Integer gameId) {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();

    GameResponse.GameData gameData = gameService.getGame(userId, gameId);
    log.info("게임 id로 게임 조회 - userId : {},  gameId : {}", userId, gameId);

    return ResponseEntity.ok(GameResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("get Game data successfully")
        .data(gameData) // 게임 데이터 반환
        .timestamp(LocalDateTime.now().toString())
        .build());
  }


  /**
   * 유저를 통한 게임 조회
   **/
  @GetMapping
  public ResponseEntity<GameListResponse> getGameList(
      @RequestParam(required = false) Integer count) {

    CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    int userId = userDetails.getUser().getId();

    GameListResponse.GameListData listData = gameService.getGameList(userId, count);
    log.info("유저를 통한 게임 조회 - userId : {},  count : {}", userId, count);

    return ResponseEntity.ok(GameListResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("Game list retrieved successfully")
        .data(listData)
        .timestamp(LocalDateTime.now().toString())
        .build());
  }
}


