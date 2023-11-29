package com.soeun.GiftFunding.dto;

import com.soeun.GiftFunding.entity.FundingProduct;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDay;

    private List<FundingProduct> fundingProductList;
}
