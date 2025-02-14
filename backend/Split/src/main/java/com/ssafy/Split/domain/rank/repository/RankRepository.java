package com.ssafy.Split.domain.rank.repository;

import com.ssafy.Split.domain.rank.domain.entity.Rank;
import com.ssafy.Split.domain.rank.domain.entity.RankId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RankRepository extends JpaRepository<Rank, RankId> {

  @Query("SELECT r FROM Rank r ORDER BY r.poseAvgscore DESC")
  List<Rank> findAllOrderByPoseAvgscoreDesc();

  /**
   * 기존 랭킹 데이터 삭제
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM Rank")
  void deleteAllRanks();

}
