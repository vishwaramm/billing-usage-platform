package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByTenantIdAndSubscriptionIdAndPeriodStartAndPeriodEnd(
            UUID tenantId, UUID subscriptionId, LocalDate periodStart, LocalDate periodEnd);
}
