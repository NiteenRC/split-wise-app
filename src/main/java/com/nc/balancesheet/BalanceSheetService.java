package com.nc.balancesheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nc.payment.Payment;
import com.nc.payment.PaymentRepository;
import com.nc.user.User;
import org.springframework.stereotype.Service;

@Service
public class BalanceSheetService {

    private final PaymentRepository paymentRepository;

    public BalanceSheetService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Map<User, Double> generateBalanceSheet() {
        List<Payment> payments = paymentRepository.findAll();
        Map<User, Double> balanceSheet = new HashMap<>();

        for (Payment payment : payments) {
            User payer = payment.getPayerId();
            User payee = payment.getPayeeId();
            Double amount = payment.getAmount();

            // Update balance for payer
            balanceSheet.put(payer, balanceSheet.getOrDefault(payer, 0.0) - amount);

            // Update balance for payee
            balanceSheet.put(payee, balanceSheet.getOrDefault(payee, 0.0) + amount);
        }

        return balanceSheet;
    }

    public Map<User, Double> generateBalanceSheetForUser(User user) {
        List<Payment> payments = paymentRepository.findByPayerIdOrPayeeId(user, user);
        Map<User, Double> balanceSheet = new HashMap<>();

        for (Payment payment : payments) {
            User payer = payment.getPayerId();
            User payee = payment.getPayeeId();
            Double amount = payment.getAmount();

            // Determine whether the user is the payer or payee
            User targetUser = payer.equals(user) ? payee : payer;

            // Update balance for the target user
            balanceSheet.put(targetUser, balanceSheet.getOrDefault(targetUser, 0.0) + amount);
        }

        return balanceSheet;
    }
}
