package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.dto.FriendListResponse;
import com.soeun.GiftFunding.dto.FriendRequestList;
import com.soeun.GiftFunding.dto.FundingProductDto;
import com.soeun.GiftFunding.type.FriendState;
import java.time.LocalDateTime;
import java.util.List;
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
    private Member memberReqId;

    @Enumerated(EnumType.STRING)
    private FriendState friendState;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public FriendRequestList toFriendReqDto() {
        return FriendRequestList.builder()
            .memberName(this.memberReqId.getName())
            .memberEmail(this.memberReqId.getEmail())
            .createdAt(this.createdAt)
            .build();
    }

    public FriendListResponse toFriendDto(
        List<FundingProductDto> fundingProductList) {
        return FriendListResponse.builder()
            .name(this.memberReqId.getName())
            .phone(this.memberReqId.getPhone())
            .email(this.memberReqId.getEmail())
            .birthDay(this.memberReqId.getBirthDay())
            .createdAt(LocalDateTime.now())
            .fundingProductList(fundingProductList)
            .build();
    }
}
