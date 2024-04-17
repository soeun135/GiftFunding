package com.soeun.GiftFunding.domain.friend.dto;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendList {

    private String memberName;
    private String memberEmail;

    private LocalDateTime createdAt;
}
