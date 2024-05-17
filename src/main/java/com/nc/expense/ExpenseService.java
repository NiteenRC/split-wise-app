package com.nc.expense;

import com.nc.expenseDetails.ExpenseDetails;
import com.nc.expenseDetails.ExpenseDetailsRepository;
import com.nc.group.Group;
import com.nc.model.ExpenseModel;
import com.nc.model.SplitType;
import com.nc.payment.Payment;
import com.nc.payment.PaymentService;
import com.nc.split.Split;
import com.nc.split.SplitRepository;
import com.nc.split.SplitService;
import com.nc.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private ExpenseDetailsRepository expenseDetailsRepository;
    private PaymentService PaymentService;
    private SplitService splitService;
    private SplitRepository splitRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public Expense saveOrUpdateExpense(ExpenseModel expenseModel) {
        // Set split type - As of now it's default
        expenseModel.getExpense().setSplitType(SplitType.EQUAL);
        Expense savedExpense = expenseRepository.save(expenseModel.getExpense());

        // Split the expense amount between users
        splitExpenseAmongUsers(expenseModel);

        // Save expense details
        saveExpenseDetails(expenseModel, savedExpense);

        return savedExpense;
    }

    private void splitExpenseAmongUsers(ExpenseModel expenseModel) {
        List<User> splitBetweenUsers = expenseModel.getSplitBetweenUsers();
        double splitAmount = expenseModel.getAmountPaid() / splitBetweenUsers.size();

        for (User user : splitBetweenUsers) {
            double splitAmountForUser = user.equals(expenseModel.getPayer()) ?
                    expenseModel.getAmountPaid() - splitAmount :
                    -splitAmount;

            savePayment(expenseModel.getPayer(), user, splitAmountForUser, expenseModel.getExpense());
        }
    }

    private void savePayment(User payer, User payee, double amount, Expense expense) {
        Payment Payment = new Payment();
        Payment.setPayer(payer);
        Payment.setPayee(payee);
        Payment.setAmount(amount);
        Payment.setExpense(expense);
        PaymentService.saveOrUpdateTransaction(Payment);
    }

    private void saveExpenseDetails(ExpenseModel expenseModel, Expense savedExpense) {
        ExpenseDetails expenseDetails = new ExpenseDetails();
        expenseDetails.setExpense(savedExpense);
        expenseDetails.setPayer(expenseModel.getPayer());
        expenseDetails.setAmountPaid(expenseModel.getAmountPaid());
        expenseDetailsRepository.save(expenseDetails);
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


