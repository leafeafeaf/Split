package com.ssafy.Split.domain.rank.controller;

import com.ssafy.Split.domain.rank.domain.dto.response.RankingResponse;
import com.ssafy.Split.domain.rank.service.RankService;
import com.ssafy.Split.global.common.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
@Slf4j
public class RankController {

    private final RankService rankService;

    @GetMapping
    public ResponseEntity<ErrorResponse> getRanking() {
        List<RankingResponse.RankData> rankList = rankService.getRanking();

        return ResponseEntity.ok(ErrorResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("get ranking successfully")
                .data(rankList)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
