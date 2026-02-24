package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.PlanMeteredPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanMeteredPriceRepository extends JpaRepository<PlanMeteredPrice, UUID> {
    List<PlanMeteredPrice> findByTenantIdAndPlanId(UUID tenantId, UUID planId);
}
