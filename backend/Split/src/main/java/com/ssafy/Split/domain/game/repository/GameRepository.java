package com.ssafy.Split.domain.game.repository;


import com.ssafy.Split.domain.game.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game,Integer> {
    List<Game> findByUserIdOrderByGameDateDesc(Integer userId);

    @Query("SELECT g FROM Game g WHERE g.user.id = :userId ORDER BY g.gameDate DESC LIMIT :limit")
    List<Game> findTopNByUserIdOrderByGameDateDesc(Integer userId, Integer limit);
}
