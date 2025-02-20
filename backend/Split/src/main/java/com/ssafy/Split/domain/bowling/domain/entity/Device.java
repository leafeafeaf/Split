package com.ssafy.Split.domain.bowling.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "device")
public class Device {
    @Id
    private Integer serialNumber;

    @OneToMany(mappedBy = "device")
    private List<Progress> progresses = new ArrayList<>();

    @OneToMany(mappedBy = "device")
    private List<Frame> frames = new ArrayList<>();
}