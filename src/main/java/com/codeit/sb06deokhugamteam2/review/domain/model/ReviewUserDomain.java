package com.codeit.sb06deokhugamteam2.review.domain.model;

import java.util.UUID;

public class ReviewUserDomain {

    private final UUID id;
    private String nickname;

    public ReviewUserDomain(UUID id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public UUID id() {
        return id;
    }

    public String nickname() {
        return nickname;
    }
}
