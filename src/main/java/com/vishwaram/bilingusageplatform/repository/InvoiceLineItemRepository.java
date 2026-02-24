package com.vishwaram.bilingusageplatform.repository;

import com.vishwaram.bilingusageplatform.domain.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, UUID> {
}
