package com.nc.expense;

import com.nc.expenseDetails.ExpenseDetails;
import com.nc.expenseDetails.ExpenseDetailsRepository;
import com.nc.group.Group;
import com.nc.model.*;
import com.nc.payment.Payment;
import com.nc.payment.PaymentService;
import com.nc.split.Split;
import com.nc.split.SplitRepository;
import com.nc.split.SplitService;
import com.nc.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseDetailsRepository expenseDetailsRepository;
    private final PaymentService paymentService;
    private final SplitService splitService;
    private final SplitRepository splitRepository;

    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(ExpenseDtoConverter::convertToDto)
                .collect(Collectors.toList());
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public List<Expense> saveOrUpdateExpense(UserExpenseModel userExpenseModel, String type) {
        double totalAmountPaid = userExpenseModel.getPayers().stream()
                .mapToDouble(UserModel::getAmountPaid)
                .sum();

        if (totalAmountPaid != userExpenseModel.getExpenseModel().getExpense().getExpenseAmount()) {
            throw new RuntimeException("Please pay the whole amount");
        }

        List<Expense> expenses = new ArrayList<>();
        Expense expense = userExpenseModel.getExpenseModel().getExpense();

        switch (type) {
            case "EQUAL" -> {
                expense.setSplitType(SplitType.EQUAL);
                expenses.add(getExpenseForEqual(userExpenseModel));
            }
            case "NON_EQUAL" -> {
                expense.setSplitType(SplitType.NON_EQUAL);
                expenses = userExpenseModel.getPayers().stream()
                        .map(userModel -> {
                            userExpenseModel.getExpenseModel().setPayer(userModel.getPayer());
                            userExpenseModel.getExpenseModel().setAmountPaid(userModel.getAmountPaid());
                            return getExpenseForEqual(userExpenseModel);
                        })
                        .collect(Collectors.toList());
            }
            default -> throw new RuntimeException("SplitType not Valid");
        }
        return expenses;
    }

    private Expense getExpenseForEqual(UserExpenseModel userExpenseModel) {
        ExpenseModel expenseModel = userExpenseModel.getExpenseModel();
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

        splitBetweenUsers.stream()
                .map(user -> new PaymentDetail(
                        expenseModel.getPayer(),
                        user,
                        user.equals(expenseModel.getPayer()) ? expenseModel.getAmountPaid() - splitAmount : -splitAmount,
                        expenseModel.getExpense()
                ))
                .forEach(paymentDetail -> savePayment(
                        paymentDetail.payer(),
                        paymentDetail.payee(),
                        paymentDetail.amount(),
                        paymentDetail.expense()
                ));
    }

    private void savePayment(User payer, User payee, double amount, Expense expense) {
        Payment payment = new Payment();
        payment.setPayer(payer);
        payment.setPayee(payee);
        payment.setAmount(amount);
        payment.setExpense(expense);
        paymentService.saveOrUpdateTransaction(payment);
    }

    private void saveExpenseDetails(ExpenseModel expenseModel, Expense savedExpense) {
        ExpenseDetails expenseDetails = new ExpenseDetails();
        expenseDetails.setExpense(savedExpense);
        expenseDetails.setPayer(expenseModel.getPayer());
        expenseDetails.setAmountPaid(expenseModel.getAmountPaid());
        expenseDetailsRepository.save(expenseDetails);
    }

    private void updateGroupSplit(Group group, User user, double splitAmount) {
        Optional<Split> existingSplitOptional = splitRepository.findByGroupAndUser(group, user);

        Split splitToUpdate = existingSplitOptional.orElseGet(() -> {
            Split newSplit = new Split();
            newSplit.setGroup(group);
            newSplit.setUser(user);
            return newSplit;
        });

        double currentSplitAmount = Optional.ofNullable(splitToUpdate.getSplitAmount()).orElse(0.0);
        double newSplitAmount = currentSplitAmount + splitAmount;
        splitToUpdate.setSplitAmount(newSplitAmount);

        splitService.saveOrUpdateSplit(splitToUpdate);
    }

    private record PaymentDetail(User payer, User payee, double amount, Expense expense) {
    }
}
