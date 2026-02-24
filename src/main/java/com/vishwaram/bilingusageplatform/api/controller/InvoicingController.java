package com.vishwaram.bilingusageplatform.api.controller;

import com.vishwaram.bilingusageplatform.api.dto.RunInvoicesResponse;
import com.vishwaram.bilingusageplatform.service.InvoicingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/invoices")
public class InvoicingController {
    private final InvoicingService invoicingService;

    public InvoicingController(InvoicingService invoicingService) {
        this.invoicingService = invoicingService;
    }

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
}
