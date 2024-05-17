package com.nc.payment;

import com.nc.group.GroupRepository;
import com.nc.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private GroupRepository groupRepository;

    public List<Payment> getAllTransactions() {
        return paymentRepository.findAll();
    }

    public Payment getTransactionById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment saveOrUpdateTransaction(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deleteTransaction(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<PaymentDTO> getTotalExpenseByUserAndGroupName(String groupName) {
        List<Payment> payments = null;//paymentRepository.findB(groupName);
        List<PaymentDTO> totalExpenseByUser = new ArrayList<>();

        for (Payment expense : payments) {
            String userName = expense.getPayerId().getUsername();
            Double expenseAmount = expense.getAmount();

            // Check if user exists in the list
            PaymentDTO userExpenseDTO = totalExpenseByUser.stream()
                    .filter(dto -> dto.getUsername().equals(userName))
                    .findFirst()
                    .orElse(null);

            // If user doesn't exist in the list, create a new UserExpenseDTO
            if (userExpenseDTO == null) {
                userExpenseDTO = new PaymentDTO(userName, expenseAmount);
                totalExpenseByUser.add(userExpenseDTO);
            } else {
                // If user already exists in the list, update the expense amount
                userExpenseDTO.setTotalExpense(userExpenseDTO.getTotalExpense() + expenseAmount);
            }
        }

        return totalExpenseByUser;
    }

    public List<Payment> getExpenseByName(String expenseName) {
        return paymentRepository.findByExpenseExpenseName(expenseName);
    }

    public List<PaymentDTO> generateBalanceSheetForGroup(Long groupId) {
        List<User> users = groupRepository.findAllUsersById(groupId);
        List<Payment> payments = paymentRepository.findByPayerIdInOrPayeeIdIn(users, users);
        List<PaymentDTO> balanceSheet = new ArrayList<>();

        for (Payment payment : payments) {
            User payer = payment.getPayerId();
            User payee = payment.getPayeeId();
            Double amount = payment.getAmount();

            if (users.contains(payer)) {
                updateBalance(balanceSheet, payer.getUsername(), -amount);
            }
            if (users.contains(payee)) {
                updateBalance(balanceSheet, payee.getUsername(), amount);
            }
        }

        return balanceSheet;
    }

    private void updateBalance(List<PaymentDTO> balanceSheet, String userName, Double amount) {
        PaymentDTO entry = balanceSheet.stream()
                .filter(dto -> dto.getUsername().equals(userName))
                .findFirst()
                .orElse(null);

        if (entry == null) {
            entry = new PaymentDTO(userName, amount);
            balanceSheet.add(entry);
        } else {
            entry.setTotalExpense(entry.getTotalExpense() + amount);
        }
    }
}