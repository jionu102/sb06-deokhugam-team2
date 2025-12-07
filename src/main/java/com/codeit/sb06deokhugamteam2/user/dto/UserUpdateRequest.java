package com.codeit.sb06deokhugamteam2.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해야 합니다.")
        private final String nickname;

    @JsonCreator
    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}
