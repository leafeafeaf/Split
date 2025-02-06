package com.ssafy.Split.bowling.repository;

import com.ssafy.Split.bowling.domain.entity.Frame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrameRepository extends JpaRepository<Frame,Integer> {
    Integer countByProgressId(Integer id);

    Optional<Frame> findByProgressIdAndNum(Integer id, Integer frameNum);
}
