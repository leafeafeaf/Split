package com.ssafy.Split.domain.game.repository;


import com.ssafy.Split.domain.game.domain.entity.Game;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    List<Game> findByUserIdOrderByGameDateDesc(Integer userId);

    @Query("SELECT g FROM Game g WHERE g.user.id = :userId ORDER BY g.gameDate DESC LIMIT :limit")
    List<Game> findTopNByUserIdOrderByGameDateDesc(Integer userId, Integer limit);

    /**
     * 최근 3개월 내 상위 5000명 게임 데이터 조회
     */
    @Modifying
    @Query(value = """
            INSERT INTO game_rank (game_id, user_id, nickname, total_game_count, game_date,
                                       pose_highscore, pose_lowscore, pose_avgscore,
                                       elbow_angle_score, arm_stability_score, arm_speed, highlight)
            SELECT g.id, g.user_id, u.nickname, u.total_game_count, g.game_date,
                   g.pose_highscore, g.pose_lowscore, g.pose_avgscore,
                   g.elbow_angle_score, g.arm_stability_score, g.arm_speed,
                   u.highlight
            FROM game g
            JOIN user u ON g.user_id = u.id
            WHERE g.user_id IN (
                SELECT g2.user_id
                FROM game g2
                WHERE g2.game_date >= :oneYearAgo
                AND g2.is_skip = false
                GROUP BY g2.user_id
                HAVING COUNT(g2.user_id) >= 5
            )
            AND g.pose_avgscore = (
                SELECT MAX(g3.pose_avgscore)
                FROM game g3
                WHERE g3.user_id = g.user_id
            )
            ORDER BY g.pose_avgscore DESC;
            
            """, nativeQuery = true)
    void insertTopRankedGames(@Param("oneYearAgo") LocalDateTime oneYearAgo);


}
