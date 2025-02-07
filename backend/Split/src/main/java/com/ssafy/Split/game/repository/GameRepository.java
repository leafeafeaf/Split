package com.ssafy.Split.game.repository;


import com.ssafy.Split.game.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Integer> {
}
