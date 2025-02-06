package com.ssafy.Split.bowling.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoUploadRequest {

    @NotEmpty(message = "Video URL cannot be empty")
    private String video;
}

