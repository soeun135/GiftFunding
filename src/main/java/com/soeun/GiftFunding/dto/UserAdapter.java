package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.User;
import lombok.Getter;

@Getter
public class UserAdapter extends User {
    private User user;

    public UserAdapter(User user) {
        super(user.getId(), user.getName(), user.getPhone(),
            user.getEmail(), user.getPassword(), user.getAddress()
        , user.getBirthDay(), user.getOAuthProvider(), user.getCreatedAt(),
            user.getUpdatedAt());
        this.user = user;
    }
}
