package com.nc.expense;

import com.nc.group.Group;
import com.nc.split.Split;
import com.nc.split.SplitRepository;
import com.nc.split.SplitService;
import com.nc.payment.Payment;
import com.nc.payment.PaymentService;
import com.nc.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private PaymentService transactionService;
    @Autowired
    private SplitService splitService;
    @Autowired
    private SplitRepository splitRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public Expense saveOrUpdateExpense(Expense expense) {
        Expense savedExpense = expenseRepository.save(expense);
        List<User> splitBetweenUsers = expense.getSplitBetweenUsers();
        double splitAmount = expense.getAmountPaid() / splitBetweenUsers.size();

        // Create Payment records for each user
        for (User user : splitBetweenUsers) {
            Payment transaction = new Payment();
            transaction.setPayerId(expense.getUser());
            transaction.setPayeeId(user);
            transaction.setExpense(savedExpense);

            double splitAmountForUser = splitAmount;
            if (user.getId().equals(expense.getUser().getId())) {
                transaction.setAmount(expense.getAmountPaid() - splitAmount);
            } else {
                splitAmountForUser = -1 * splitAmount;
                transaction.setAmount(splitAmountForUser);
            }
            transactionService.saveOrUpdateTransaction(transaction);
        }
        return savedExpense;
    }

    private void updateGroupSplit(Group group, User user, double splitAmount) {
        // Find the existing split record for the user and group
        Optional<Split> existingSplitOptional = splitRepository.findByGroupAndUser(group, user);

        Split splitToUpdate;
        if (existingSplitOptional.isPresent()) {
            // If a split record exists for the user in the group, update it
            splitToUpdate = existingSplitOptional.get();
        } else {
            // If no split record exists for the user in the group, create a new one
            splitToUpdate = new Split();
            splitToUpdate.setGroup(group);
            splitToUpdate.setUser(user);
        }

        // Update the split amount
        double currentSplitAmount = splitToUpdate.getSplitAmount() == null ? 0 : splitToUpdate.getSplitAmount();
        double newSplitAmount = currentSplitAmount + splitAmount;
        splitToUpdate.setSplitAmount(newSplitAmount);

        // Save or update the split record
        splitService.saveOrUpdateSplit(splitToUpdate);
    }

}


