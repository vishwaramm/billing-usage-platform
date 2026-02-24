package com.vishwaram.bilingusageplatform.api.controller;

import com.vishwaram.bilingusageplatform.api.dto.InvoiceDetailsResponse;
import com.vishwaram.bilingusageplatform.api.dto.InvoiceLineItemResponse;
import com.vishwaram.bilingusageplatform.api.dto.InvoiceSummaryResponse;
import com.vishwaram.bilingusageplatform.api.dto.RunInvoicesResponse;
import com.vishwaram.bilingusageplatform.domain.Invoice;
import com.vishwaram.bilingusageplatform.domain.InvoiceLineItem;
import com.vishwaram.bilingusageplatform.infra.TenantContext;
import com.vishwaram.bilingusageplatform.repository.InvoiceLineItemRepository;
import com.vishwaram.bilingusageplatform.repository.InvoiceRepository;
import com.vishwaram.bilingusageplatform.service.InvoicingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineItemRepository invoiceLineItemRepository;
    private final InvoicingService invoicingService;

    public InvoiceController(
            InvoiceRepository invoiceRepository,
            InvoiceLineItemRepository invoiceLineItemRepository,
            InvoicingService invoicingService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineItemRepository = invoiceLineItemRepository;
        this.invoicingService = invoicingService;
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceSummaryResponse>> list(
            @RequestParam("period") String period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID tenantId = TenantContext.getRequired();
        YearMonth ym = YearMonth.parse(period);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Invoice> invoices = invoiceRepository.findByTenantIdAndPeriodStartAndPeriodEnd(
                tenantId,
                start,
                end,
                pageable
        );
        Page<InvoiceSummaryResponse> mapped = invoices.map(this::toSummary);
        return ResponseEntity.ok(mapped);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetailsResponse> get(@PathVariable("id") UUID id) {
        UUID tenantId = TenantContext.getRequired();

        Invoice invoice = invoiceRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        List<InvoiceLineItem> items = invoiceLineItemRepository.findByTenantIdAndInvoiceId(tenantId, invoice.getId());
        return ResponseEntity.ok(toDetails(invoice, items));
    }

    @GetMapping("/run")
    public ResponseEntity<RunInvoicesResponse> run(@RequestParam("period") String period) {
        YearMonth ym = YearMonth.parse(period);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);
        List<UUID> ids = invoicingService.runForPeriod(start, end);

        return ResponseEntity.ok(new RunInvoicesResponse(
                start.toString(),
                end.toString(),
                ids.size(),
                ids
        ));
    }

    private InvoiceDetailsResponse toDetails(Invoice inv, List<InvoiceLineItem> items) {
        List<InvoiceLineItemResponse> itemDetails = items.stream().map(i ->
                new InvoiceLineItemResponse(
                    i.getId(),
                    i.getKind().name(),
                    i.getMetric(),
                    i.getDescription(),
                    i.getQuantity(),
                    i.getUnitPriceCents(),
                    i.getAmountCents()
            )).toList();

        return new InvoiceDetailsResponse(
                inv.getId(),
                inv.getSubscriptionId(),
                inv.getPeriodStart(),
                inv.getPeriodEnd(),
                inv.getStatus().name(),
                inv.getCurrency(),
                inv.getTotalCents(),
                itemDetails
        );
    }

    private InvoiceSummaryResponse toSummary(Invoice i) {
        return new InvoiceSummaryResponse(
                i.getId(),
                i.getSubscriptionId(),
                i.getPeriodStart(),
                i.getPeriodEnd(),
                i.getStatus().name(),
                i.getCurrency(),
                i.getTotalCents()
        );
    }
}
