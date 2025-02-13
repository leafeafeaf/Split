package com.ssafy.Split.domain.user.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThemaRequest {
    private Integer thema;

    // 유효성 검증: 1, 2가 아닌 경우 2로 설정
    public Integer getValidThema() {
        return (thema != null && thema == 1) ? 1 : 2;
    }
}


