package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserProfileResponse {

    private final Long id;
    private final String email;
    private final String nickName;
    private final String imageUrl;


    public UserProfileResponse(Long id, String email, String nickName, String imageUrl) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
    }
}
