package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    Optional<Plan> findByTenantIdAndId(UUID tenantId, UUID id);
}
