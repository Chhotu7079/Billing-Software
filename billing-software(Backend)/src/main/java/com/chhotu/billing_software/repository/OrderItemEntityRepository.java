package com.chhotu.billing_software.repository;

import com.chhotu.billing_software.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, Long> {

}
