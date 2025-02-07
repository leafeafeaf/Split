package com.ssafy.Split.domain.bowling.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadResponse {
    private String code;
    private int status;
    private String message;
    private String timestamp;
}
