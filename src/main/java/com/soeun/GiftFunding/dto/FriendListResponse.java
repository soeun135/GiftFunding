package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.FundingProduct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
public class FriendListResponse {
    private String name;
    private String phone;
    private String email;
    private LocalDate birthDay;
    private LocalDateTime createdAt;

    private List<FundingProduct> fundingProductList;

}
