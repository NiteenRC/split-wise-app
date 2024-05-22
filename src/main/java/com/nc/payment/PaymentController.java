package com.nc.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/balance-sheet")
    public ResponseEntity<?> paymentSummary() {
        List<PaymentSummary> paymentSummary = paymentService.getPaymentSummaries();
        return ResponseEntity.ok().body(paymentSummary);
    }

    @GetMapping
    public ResponseEntity<?> balanceSheet() {
        BalanceSheetDTO balanceSheetDTO = paymentService.getGroupBalanceSheet();
        return ResponseEntity.ok().body(balanceSheetDTO);
    }
}