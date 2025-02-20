package com.ssafy.Split.domain.game.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUploadResponse {

    private String code;
    private int status;
    private String message;
    private String timestamp;
    private GameData data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameData {
        private Integer id;
    }




}
