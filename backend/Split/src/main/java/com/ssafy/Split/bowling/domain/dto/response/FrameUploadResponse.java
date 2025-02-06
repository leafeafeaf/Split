package com.ssafy.Split.bowling.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrameUploadResponse {
    private String code;
    private int status;
    private String message;
    private String timestamp;
    private FrameData data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrameData {
        private Integer num;
    }
}
