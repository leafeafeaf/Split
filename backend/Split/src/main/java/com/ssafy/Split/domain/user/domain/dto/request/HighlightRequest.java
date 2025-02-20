package com.ssafy.Split.domain.user.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HighlightRequest {
    @NotEmpty(message = "Highlight URL cannot be empty")
    private String highlight;
}
