package com.soeun.GiftFunding.domain.friend.dto;

import java.time.LocalDate;

import com.soeun.GiftFunding.dto.FundingProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendFundingProduct {
    private String name;
    private String phone;
    private String email;
    private LocalDate birthDay;

    private Page<FundingProductDto> fundingProductList;

}
