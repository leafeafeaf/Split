package com.ssafy.Split.domain.game.repository;


import com.ssafy.Split.domain.game.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Integer> {
}
