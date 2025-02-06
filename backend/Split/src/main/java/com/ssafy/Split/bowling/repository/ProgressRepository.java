package com.ssafy.Split.bowling.repository;

import com.ssafy.Split.bowling.domain.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgressRepository extends JpaRepository<Progress,Integer> {


    // 디바이스가 사용중인지 확인 (IN_PROGRESS 상태의 progress 존재 여부)
    @Query("SELECT COUNT(p) > 0 FROM Progress p WHERE p.device.serialNumber = :serialNumber")
    boolean existsByDeviceSerialNumberAndStatusInProgress(@Param("serialNumber") Integer serialNumber);
}
