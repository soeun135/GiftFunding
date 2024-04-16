package com.soeun.GiftFunding.domain.friend.entity;

import com.soeun.GiftFunding.domain.friend.dto.FriendList;
import com.soeun.GiftFunding.domain.member.entity.Member;
import com.soeun.GiftFunding.type.FriendState;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "member_req_id")
    private Member memberRequest;

    @Enumerated(EnumType.STRING)
    private FriendState friendState;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public FriendList toFriendReqDto() {
        return FriendList.builder()
            .memberName(this.memberRequest.getName())
            .memberEmail(this.memberRequest.getEmail())
            .createdAt(this.createdAt)
            .build();
    }
}
