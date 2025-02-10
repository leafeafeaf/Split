package com.ssafy.Split.domain.rank.repository;

import com.ssafy.Split.domain.rank.domain.entity.Rank;
import com.ssafy.Split.domain.rank.domain.entity.RankId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankRepository extends JpaRepository<Rank, RankId> {
    @Query("SELECT r FROM Rank r ORDER BY r.poseAvgscore DESC")
    List<Rank> findAllOrderByPoseAvgscoreDesc();
}
