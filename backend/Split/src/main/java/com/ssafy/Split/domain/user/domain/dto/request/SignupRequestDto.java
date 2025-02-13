package com.ssafy.Split.domain.user.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    //TODO 더 복잡한 검증 로직 필요

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 5, message = "비밀번호는 최소 5자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @NotNull(message = "성별은 필수 입력값입니다.")
    @Min(value = 1, message = "성별 값은 1(남자), 2(여자), 3(미정) 중 하나여야 합니다.")
    @Max(value = 3, message = "성별 값은 1(남자), 2(여자), 3(미정) 중 하나여야 합니다.")
    private Integer gender;

    private Integer height; // 선택 입력
}
