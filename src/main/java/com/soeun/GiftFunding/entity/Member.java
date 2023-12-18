package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.dto.UpdateInfo;
import com.soeun.GiftFunding.type.OAuthProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String address;

    @Column
    private LocalDate birthDay;

    @Column
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public UpdateInfo.Response toDto() {
        return UpdateInfo.Response.builder()
            .name(this.getName())
            .email(this.getEmail())
            .phone(this.getPhone())
            .address(this.getAddress())
            .birthDay(this.getBirthDay())
            .build();
    }
}
