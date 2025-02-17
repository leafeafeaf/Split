package com.ssafy.Split.domain.rank.service;

import com.ssafy.Split.domain.rank.domain.dto.response.RankingResponse;
import com.ssafy.Split.domain.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankService {

    private final RankRepository rankRepository;

    public List<RankingResponse.RankData> getRanking() {
        return rankRepository.findAllOrderByPoseAvgscoreDesc()
                .stream()
                .map(rank -> RankingResponse.RankData.builder()
                        .gameId(rank.getGame().getId())
                        .userId(rank.getUser().getId())
                        .nickname(rank.getNickname())
                        .highlight(rank.getHighlight())
                        .totalGameCount(rank.getTotalGameCount())
                        .gameDate(rank.getGameDate().toString())
                        .poseHighscore(rank.getPoseHighscore())
                        .poseLowscore(rank.getPoseLowscore())
                        .poseAvgscore(rank.getPoseAvgscore())
                        .elbowAngleScore(rank.getElbowAngleScore())
                        .armStabilityScore(rank.getArmStabilityScore())
                        .armSpeed(rank.getArmSpeed())
                        .build())
                .collect(Collectors.toList());
    }
}