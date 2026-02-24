package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.InvoiceRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRunRepository extends JpaRepository<InvoiceRun, UUID> {
    Optional<InvoiceRun> findByTenantIdAndPeriodStartAndPeriodEnd(
            UUID tenantId, LocalDate periodStart, LocalDate periodEnd
    );
}
