package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.dto.FriendList;
import com.soeun.GiftFunding.type.FriendState;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
