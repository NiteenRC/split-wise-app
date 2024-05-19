package com.nc.payment;

import com.nc.group.GroupRepository;
import com.nc.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;

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
        // Assuming there is a method in paymentRepository to find payments by group name
        List<Payment> payments = null;//paymentRepository.findByGroupName(groupName);
        List<PaymentDTO> totalExpenseByUser = new ArrayList<>();

        payments.stream()
                .collect(Collectors.groupingBy(payment -> payment.getPayer().getUsername(),
                        Collectors.summingDouble(Payment::getAmount)))
                .forEach((userName, totalAmount) ->
                        totalExpenseByUser.add(new PaymentDTO(userName, totalAmount)));

        return totalExpenseByUser;
    }

    public List<Payment> getExpenseByName(String expenseName) {
        return paymentRepository.findByExpenseExpenseName(expenseName);
    }

    public List<PaymentDTO> generateBalanceSheetForGroup(Long groupId) {
        List<User> users = groupRepository.findAllUsersById(groupId);
        List<Payment> payments = paymentRepository.findByPayerInOrPayeeIn(users, users);
        List<PaymentDTO> balanceSheet = new ArrayList<>();

        payments.forEach(payment -> {
            updateBalance(balanceSheet, payment.getPayer().getUsername(), -payment.getAmount());
            updateBalance(balanceSheet, payment.getPayee().getUsername(), payment.getAmount());
        });

        return balanceSheet;
    }

    private void updateBalance(List<PaymentDTO> balanceSheet, String userName, Double amount) {
        balanceSheet.stream()
                .filter(dto -> dto.getUsername().equals(userName))
                .findFirst()
                .ifPresentOrElse(
                        entry -> entry.setTotalExpense(entry.getTotalExpense() + amount),
                        () -> balanceSheet.add(new PaymentDTO(userName, amount))
                );
    }
}