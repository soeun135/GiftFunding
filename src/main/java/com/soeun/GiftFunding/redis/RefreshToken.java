package com.soeun.GiftFunding.redis;


import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String refreshToken;

    private String mail;
}
