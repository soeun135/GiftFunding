package com.soeun.GiftFunding.scheduler;

import com.soeun.GiftFunding.entity.Delivery;
import com.soeun.GiftFunding.entity.FundingProduct;
import com.soeun.GiftFunding.repository.DeliveryRepository;
import com.soeun.GiftFunding.repository.FundingProductRepository;
import com.soeun.GiftFunding.type.DeliveryState;
import com.soeun.GiftFunding.type.FundingState;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryScheduler {
    private final DeliveryRepository deliveryRepository;
    private final FundingProductRepository fundingProductRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void checkDelivery() {
        List<Delivery> deliveryList =
            deliveryRepository.findByDeliveryState(
                DeliveryState.WAIT);

        for (Delivery d : deliveryList) {
            d.setDeliveryState(DeliveryState.SUCCESS);

            FundingProduct fundingProduct =
                fundingProductRepository.getReferenceById(
                d.getFundingProduct().getId());

            fundingProduct.setFundingState(
                FundingState.DELIVERY_SUCCESS);
        }
    }
}
