package com.nc.expense;

import com.nc.group.Group;
import com.nc.split.Split;
import com.nc.split.SplitRepository;
import com.nc.split.SplitService;
import com.nc.transaction.Transaction;
import com.nc.transaction.TransactionService;
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
    private TransactionService transactionService;
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
        double splitAmount = expense.getExpenseAmount() / splitBetweenUsers.size();

        // Create transaction records for each user
        for (User user : splitBetweenUsers) {
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setExpense(savedExpense);
            transaction.setGroup(expense.getGroup());
            transaction.setExpense(savedExpense);

            double splitAmountForUser = splitAmount;
            if (user.getId().equals(expense.getUser().getId())) {
                transaction.setSplitAmount(expense.getAmountPaid() - splitAmount);
            } else {
                splitAmountForUser = -1 * splitAmount;
                transaction.setSplitAmount(splitAmountForUser);
            }
            transactionService.saveOrUpdateTransaction(transaction);

            // Update split for the group
            updateGroupSplit(expense.getGroup(), user, splitAmountForUser);
        }

        return savedExpense;
    }

    private void updateGroupSplit(Group group, User user, double splitAmount) {
        Optional<Split> splitOptional = splitRepository.findByUser(user);

        Split split = splitOptional.orElseGet(Split::new);
        split.setGroup(group);
        split.setUser(user);
        double splitAmount1 = split.getSplitAmount() == null ? 0 : split.getSplitAmount();
        split.setSplitAmount(splitAmount1 + splitAmount);
        splitService.saveOrUpdateSplit(split);
    }
}


