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
    public ResponseEntity<?> balanceSheet() {
        List<PaymentSummary> b1 = paymentService.getPaymentSummaries();
        //Map<User, Double> b2 = paymentService.generateBalanceSheetForUser();
        //List<Payment> expense = paymentService.getExpenseByName(expenseName);
        return ResponseEntity.ok().body(b1);
    }
}