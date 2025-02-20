package com.ssafy.Split.domain.rank.controller;

import com.ssafy.Split.domain.rank.domain.dto.response.RankingResponse;
import com.ssafy.Split.domain.rank.service.RankService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
@Slf4j
public class RankController {

  private final RankService rankService;

  @GetMapping
  public ResponseEntity<RankingResponse> getRanking() {
    List<RankingResponse.RankData> rankList = rankService.getRanking();

    // 랭킹 데이터가 없는 경우 빈 리스트 반환
    if (rankList.isEmpty()) {
      return ResponseEntity.ok(RankingResponse.builder()
          .code("SUCCESS")
          .status(200)
          .message("No ranking data available")
          .data(Collections.emptyList())  // 빈 리스트 반환
          .timestamp(LocalDateTime.now().toString())
          .build());
    }
    log.info("랭킹 조회");

    return ResponseEntity.ok(RankingResponse.builder()
        .code("SUCCESS")
        .status(200)
        .message("get ranking successfully")
        .data(rankList)
        .timestamp(LocalDateTime.now().toString())
        .build());
  }


}
