package com.ssafy.Split.domain.bowling.repository;

import com.ssafy.Split.domain.bowling.domain.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    // serialNumber로 디바이스 조회
    Optional<Device> findBySerialNumber(Integer serialNumber);

    // 디바이스 존재 여부 확인
    boolean existsBySerialNumber(Integer serialNumber);

}
