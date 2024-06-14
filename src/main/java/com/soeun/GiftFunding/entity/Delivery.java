package com.soeun.GiftFunding.entity;

import com.soeun.GiftFunding.type.DeliveryState;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "funding_product_id")
    private FundingProduct fundingProduct;

    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;

    private LocalDateTime deliveryAt;
}
