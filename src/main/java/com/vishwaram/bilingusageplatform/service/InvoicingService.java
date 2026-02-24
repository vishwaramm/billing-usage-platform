package com.vishwaram.bilingusageplatform.service;

import com.vishwaram.bilingusageplatform.domain.*;
import com.vishwaram.bilingusageplatform.infra.TenantContext;
import com.vishwaram.bilingusageplatform.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class InvoicingService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final PlanMeteredPriceRepository planMeteredPriceRepository;
    private final UsageEventRepository usageEventRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineItemRepository invoiceLineItemRepository;
    private final InvoiceRunRepository invoiceRunRepository;

    public InvoicingService(
            SubscriptionRepository subscriptionRepository,
            PlanRepository planRepository,
            PlanMeteredPriceRepository planMeteredPriceRepository,
            UsageEventRepository usageEventRepository,
            InvoiceRepository invoiceRepository,
            InvoiceLineItemRepository invoiceLineItemRepository,
            InvoiceRunRepository invoiceRunRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.planMeteredPriceRepository = planMeteredPriceRepository;
        this.usageEventRepository = usageEventRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineItemRepository = invoiceLineItemRepository;
        this.invoiceRunRepository = invoiceRunRepository;
    }

    @Transactional
    public List<UUID> runForPeriod(LocalDate periodStart, LocalDate periodEnd) {
        UUID tenantId = TenantContext.getRequired();

        Optional<InvoiceRun> existingRun = invoiceRunRepository.findByTenantIdAndPeriodStartAndPeriodEnd(tenantId,
                periodStart, periodEnd);

        if (existingRun.isPresent()) {
            InvoiceRun run = existingRun.get();
            if (run.getStatus() == InvoiceRun.Status.COMPLETED) {
                return List.of();
            }
        } else {
            InvoiceRun run = new InvoiceRun();
            run.setTenantId(tenantId);
            run.setPeriodStart(periodStart);
            run.setPeriodEnd(periodEnd);
            run.setStatus(InvoiceRun.Status.STARTED);
            invoiceRunRepository.saveAndFlush(run);
        }

        List<Subscription> subscriptions = subscriptionRepository.findByTenantIdAndStatus(
                tenantId,
                Subscription.Status.ACTIVE.name()
            );

        List<UUID> createdInvoiceId = new ArrayList<>();

        Instant startTs = periodStart.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endTs = periodEnd.atStartOfDay(ZoneOffset.UTC).toInstant();

        for (Subscription sub : subscriptions) {
            if (sub.getPlanId() == null) continue;

            Optional<Invoice> existingInvoice =
                    invoiceRepository.findByTenantIdAndSubscriptionIdAndPeriodStartAndPeriodEnd(
                            tenantId, sub.getId(), periodStart, periodEnd
                    );

            if (existingInvoice.isPresent()) {
                // already generated
                continue;
            }

            Plan plan = planRepository.findByTenantIdAndId(tenantId, sub.getPlanId())
                    .orElseThrow(() -> new IllegalArgumentException("plan missing for subscription: " + sub.getId()));

            Map<String, Long> unitPriceByMetric = new HashMap<>();
            List<PlanMeteredPrice> meteredPrices = planMeteredPriceRepository.findByTenantIdAndPlanId(tenantId, sub.getPlanId());

            for (PlanMeteredPrice price : meteredPrices) {
                unitPriceByMetric.put(price.getMetric(), price.getUnitPriceCents());
            }

            List<UsageEventRepository.MetricSum> sums =
                    usageEventRepository.sumUnbilledByMetric(
                            tenantId,
                            sub.getId(),
                            startTs,
                            endTs
                    );

            Invoice invoice = new Invoice();
            invoice.setTenantId(tenantId);
            invoice.setSubscriptionId(sub.getId());
            invoice.setPeriodStart(periodStart);
            invoice.setPeriodEnd(periodEnd);
            invoice.setStatus(Invoice.Status.DRAFT);
            invoice.setCurrency(plan.getCurrency());

            long total = 0;

            InvoiceLineItem lineItem = new InvoiceLineItem();
            lineItem.setTenantId(tenantId);
            lineItem.setKind(InvoiceLineItem.Kind.BASE);
            lineItem.setDescription("BASE fee: " + plan.getName());
            lineItem.setQuantity(1);
            lineItem.setUnitPriceCents(plan.getBaseFeeCents());
            lineItem.setAmountCents(plan.getBaseFeeCents());
            total += lineItem.getAmountCents();

            try {
                invoice = invoiceRepository.saveAndFlush(invoice);
            } catch (DataIntegrityViolationException ex) {
                // another thread created it. treat it as idempotent
                continue;
            }

            lineItem.setInvoiceId(invoice.getId());
            invoiceLineItemRepository.save(lineItem);

            for (UsageEventRepository.MetricSum s : sums) {
                String metric = s.getMetric();
                long quantity = s.getTotalQuantity();

                if (quantity <= 0) continue;

                long unitPrice = unitPriceByMetric.getOrDefault(metric, 0L);
                long amount = Math.multiplyExact(unitPrice, quantity);

                InvoiceLineItem innerLineItem = new InvoiceLineItem();
                innerLineItem.setTenantId(tenantId);
                innerLineItem.setInvoiceId(invoice.getId());
                innerLineItem.setKind(InvoiceLineItem.Kind.METERED);
                innerLineItem.setMetric(metric);
                innerLineItem.setDescription("Metered: " + metric);
                innerLineItem.setQuantity(quantity);
                innerLineItem.setUnitPriceCents(unitPrice);
                innerLineItem.setAmountCents(amount);

                total += amount;
                invoiceLineItemRepository.save(innerLineItem);
            }

            invoice.setTotalCents(total);
            invoiceRepository.save(invoice);

            usageEventRepository.markBilled(invoice.getId(),
                    tenantId,
                    sub.getId(),
                    startTs,
                    endTs);

            createdInvoiceId.add(invoice.getId());
        }

        invoiceRunRepository.findByTenantIdAndPeriodStartAndPeriodEnd(tenantId,
                periodStart, periodEnd)
                .ifPresent(r -> {
                    r.setStatus(InvoiceRun.Status.COMPLETED);
                    r.setCompleted_at(Instant.now());
                    invoiceRunRepository.save(r);
                });

        return createdInvoiceId;
    }
}
