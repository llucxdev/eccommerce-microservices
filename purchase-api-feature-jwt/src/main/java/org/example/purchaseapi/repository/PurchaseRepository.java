package org.example.purchaseapi.repository;

import org.example.purchaseapi.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByUserId(Long userId);
}
