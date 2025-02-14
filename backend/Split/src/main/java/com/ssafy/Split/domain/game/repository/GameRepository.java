package com.ssafy.Split.domain.game.repository;


import com.ssafy.Split.domain.game.domain.entity.Game;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
  // GameRepository에 다음 메소드들을 추가해서 디버깅
  @Query("""
          SELECT COUNT(g)
          FROM Game g
          WHERE g.gameDate >= :oneYearAgo
      """)
  long countGamesInLastYear(@Param("oneYearAgo") LocalDateTime oneYearAgo);

  @Query("""
          SELECT COUNT(g)
          FROM Game g
          WHERE g.gameDate >= :oneYearAgo
          AND g.isSkip = false
      """)
  long countNotSkippedGamesInLastYear(@Param("oneYearAgo") LocalDateTime oneYearAgo);

  @Query("""
          SELECT g.user.id, COUNT(g)
          FROM Game g
          WHERE g.gameDate >= :oneYearAgo
          AND g.isSkip = false
          GROUP BY g.user.id
          HAVING COUNT(g) >= 5
      """)
  List<Object[]> countUsersWithFiveOrMoreGames(@Param("oneYearAgo") LocalDateTime oneYearAgo);


  @Query(value = """
          SELECT g FROM Game g
          WHERE g.user.id IN (
              SELECT g2.user.id
              FROM Game g2
              WHERE g2.gameDate >= :oneYearAgo
              AND g2.isSkip = false
              GROUP BY g2.user.id
              HAVING COUNT(g2) >= 5
          )
          AND g.poseAvgscore = (
            SELECT MAX(g3.poseAvgscore)
            FROM Game g3
            WHERE g3.user.id = g.user.id
            )
            ORDER BY g.poseAvgscore DESC
      """)
  List<Game> findTopRankedGames(@Param("oneYearAgo") LocalDateTime oneYearAgo);

}
