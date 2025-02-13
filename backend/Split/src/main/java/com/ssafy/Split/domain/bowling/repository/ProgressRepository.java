package com.ssafy.Split.domain.bowling.repository;

import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {

  boolean existsByDeviceSerialNumberAndTimeAfter(Integer serialNumber, LocalDateTime time);

  // Progress 조회 시 유효성 검사를 위한 메서드
  @Query("SELECT p FROM Progress p WHERE p.device.serialNumber = :serialNumber AND p.time > :timeLimit")
  Optional<Progress> findValidProgress(@Param("serialNumber") Integer serialNumber,
      @Param("timeLimit") LocalDateTime timeLimit);

  Optional<Progress> findByDeviceSerialNumber(Integer integer);
}
