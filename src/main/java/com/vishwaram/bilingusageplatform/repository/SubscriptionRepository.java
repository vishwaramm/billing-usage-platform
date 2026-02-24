package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByTenantIdAndId(UUID tenantId, UUID id);
    List<Subscription> findByTenantIdAndStatus(UUID tenantId, String status);
}
