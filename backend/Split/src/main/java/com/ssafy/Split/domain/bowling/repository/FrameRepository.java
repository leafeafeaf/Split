package com.ssafy.Split.domain.bowling.repository;

import com.ssafy.Split.domain.bowling.domain.entity.Frame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameRepository extends JpaRepository<Frame, Integer> {

  Integer countByProgressId(Integer id);

  Optional<Frame> findByProgressIdAndNum(Integer id, Integer frameNum);

  List<Frame> findAllByProgressIdOrderByNumAsc(Integer progressId);

  void deleteAllByProgressId(int progressId);

}
