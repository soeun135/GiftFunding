package com.soeun.GiftFunding.repository;

import com.soeun.GiftFunding.entity.Delivery;
import com.soeun.GiftFunding.type.DeliveryState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByDeliveryState(DeliveryState deliveryState);
}
