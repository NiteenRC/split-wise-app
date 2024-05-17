package com.nc.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/{groupName}")
    public ResponseEntity<?> getGroupById(@PathVariable String groupName) {
        List<PaymentDTO> group = paymentService.getTotalExpenseByUserAndGroupName(groupName);
        return ResponseEntity.ok().body(group);
    }

    @GetMapping("/expense/{expenseName}")
    public ResponseEntity<?> getExpenseByName(@PathVariable String expenseName) {
        List<Payment> expense = paymentService.getExpenseByName(expenseName);
        return ResponseEntity.ok().body(expense);
    }

    @GetMapping("/balance-sheet/{groupId}")
    public ResponseEntity<?> balanceSheet(@PathVariable Long groupId) {
        List<PaymentDTO> b1 = paymentService.generateBalanceSheetForGroup(groupId);
        //Map<User, Double> b2 = paymentService.generateBalanceSheetForUser();
        //List<Payment> expense = paymentService.getExpenseByName(expenseName);
        return ResponseEntity.ok().body(b1);
    }
}