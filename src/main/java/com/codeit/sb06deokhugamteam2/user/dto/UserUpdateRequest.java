package com.codeit.sb06deokhugamteam2.user.dto;

import com.codeit.sb06deokhugamteam2.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserUpdateRequest(
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해야 합니다.")
        String nickname
) {
    public void update(User user) {
        user.updateNickname(this.nickname);
    }
}
