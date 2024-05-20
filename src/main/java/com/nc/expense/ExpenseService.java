package com.nc.expense;

import com.nc.exception.ConflictException;
import com.nc.exception.CreationException;
import com.nc.exception.NotFoundException;
import com.nc.expenseDetails.ExpenseDetails;
import com.nc.expenseDetails.ExpenseDetailsRepository;
import com.nc.group.Group;
import com.nc.group.GroupRepository;
import com.nc.model.ExpenseDTO;
import com.nc.model.ExpenseDtoConverter;
import com.nc.payment.Payment;
import com.nc.payment.PaymentService;
import com.nc.split.Split;
import com.nc.split.SplitRepository;
import com.nc.split.SplitService;
import com.nc.user.User;
import com.nc.user.UserRepository;
import com.nc.user.UserRequest;
import com.nc.utility.SplitType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;
    private final ExpenseDetailsRepository expenseDetailsRepository;
    private final PaymentService paymentService;
    private final SplitService splitService;
    private final SplitRepository splitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<ExpenseDTO> getAllExpenses() {
        logger.info("Fetching all expenses");
        return expenseRepository.findAll().stream()
                .map(ExpenseDtoConverter::convertToDto)
                .collect(Collectors.toList());
    }

    public Expense getExpenseById(Long id) {
        logger.info("Fetching expense with ID {}", id);
        return expenseRepository.findById(id).orElseThrow(() -> {
            logger.error("Expense with ID {} not found", id);
            return new NotFoundException("Expense with ID " + id + " not found");
        });
    }

    public List<Expense> saveOrUpdate(ExpenseRequest expenseRequest) {
        double totalAmountPaid = expenseRequest.getPayers().stream()
                .mapToDouble(UserRequest::getUserAmountPaid)
                .sum();

        if (totalAmountPaid != expenseRequest.getExpenseAmount()) {
            logger.error("Total amount paid {} does not match the expense amount {}", totalAmountPaid, expenseRequest.getExpenseAmount());
            throw new CreationException("Expense amount should equals to the total users amount. i.e Expense amount:"
                    + expenseRequest.getExpenseAmount() + " User amountPaid:" + totalAmountPaid);
        }

        List<Expense> expenses = new ArrayList<>();
        Expense expense = new Expense();

        switch (expenseRequest.getSplitType().name()) {
            case "EQUAL" -> {
                expense.setSplitType(SplitType.EQUAL);
                expenses.add(getExpenseForEqual(expenseRequest));
            }
            case "NON_EQUAL" -> {
                expense.setSplitType(SplitType.NON_EQUAL);
                expenses = expenseRequest.getPayers().stream()
                        .map(userModel -> {
                            expenseRequest.setPayer(userModel.getPayer());
                            expenseRequest.setUserAmountPaid(userModel.getUserAmountPaid());
                            return getExpenseForEqual(expenseRequest);
                        })
                        .collect(Collectors.toList());
            }
            default -> {
                logger.error("Invalid SplitType {}", expenseRequest.getSplitType());
                throw new RuntimeException("SplitType not Valid");
            }
        }
        return expenses;
    }

    private Expense getExpenseForEqual(ExpenseRequest expenseRequest) {
        String expenseName = expenseRequest.getExpenseName().trim();
        if (expenseRepository.existsByExpenseName(expenseName)) {
            logger.error("Expense with name {} already exists", expenseName);
            throw new ConflictException("Expense with name " + expenseName + " already exists");
        }

        Optional<Group> groupOptional = groupRepository.findById(expenseRequest.getGroupId());

        if (groupOptional.isEmpty()) {
            logger.error("Group with ID {} not found", expenseRequest.getGroupId());
            throw new NotFoundException("Group with ID " + expenseRequest.getGroupId() + " not found");
        }

        Expense expense = new Expense();
        expense.setExpenseName(expenseRequest.getExpenseName());
        expense.setExpenseAmount(expenseRequest.getExpenseAmount());
        expense.setExpenseType(expenseRequest.getExpenseType());
        expense.setSplitType(expenseRequest.getSplitType());
        expense.setGroup(groupOptional.get());

        Expense savedExpense;
        try {
            savedExpense = expenseRepository.save(expense);
            logger.info("Expense with ID {} saved", savedExpense.getId());
        } catch (Exception e) {
            logger.error("Failed to create Expense: {}", e.getMessage());
            throw new CreationException("Failed to create new Expense " + e.getMessage());
        }

        // Split the expense amount between users
        splitExpenseAmongUsers(expenseRequest, savedExpense);

        // Save expense details
        saveExpenseDetails(expenseRequest, savedExpense);

        return savedExpense;
    }

    private void splitExpenseAmongUsers(ExpenseRequest expenseRequest, Expense expense) {
        List<Long> splitBetweenUsers = expenseRequest.getSplitBetweenUserIds();
        double splitAmount = expenseRequest.getUserAmountPaid() / splitBetweenUsers.size();

        splitBetweenUsers.stream()
                .map(user -> new PaymentDetail(
                        expenseRequest.getPayer(),
                        user,
                        user.equals(expenseRequest.getPayer()) ? expenseRequest.getUserAmountPaid() - splitAmount : -splitAmount,
                        expense
                ))
                .forEach(paymentDetail -> savePayment(
                        paymentDetail.payer(),
                        paymentDetail.payee(),
                        paymentDetail.amount(),
                        paymentDetail.expense()
                ));
    }

    private void savePayment(Long payerId, Long payeeId, double amount, Expense expense) {
        Optional<User> payerOptional = userRepository.findById(payerId);
        Optional<User> payeeOptional = userRepository.findById(payeeId);

        if (payerOptional.isEmpty()) {
            logger.error("Payer with ID {} not found", payerId);
            throw new NotFoundException("Payer with ID " + payerId + " not found");
        }

        if (payeeOptional.isEmpty()) {
            logger.error("Payee with ID {} not found", payeeId);
            throw new NotFoundException("Payee with ID " + payeeId + " not found");
        }

        Payment payment = new Payment();
        payment.setPayer(payerOptional.get());
        payment.setPayee(payeeOptional.get());
        payment.setAmount(amount);
        payment.setExpense(expense);
        paymentService.saveOrUpdateTransaction(payment);
        logger.info("Payment from payer ID {} to payee ID {} of amount {} saved", payerId, payeeId, amount);
    }

    private void saveExpenseDetails(ExpenseRequest expenseRequest, Expense savedExpense) {
        Optional<User> payerOptional = userRepository.findById(expenseRequest.getPayer());
        if (payerOptional.isEmpty()) {
            logger.error("Payer with ID {} not found", expenseRequest.getPayer());
            throw new NotFoundException("Payer with ID " + expenseRequest.getPayer() + " not found");
        }

        ExpenseDetails expenseDetails = new ExpenseDetails();
        expenseDetails.setExpense(savedExpense);
        expenseDetails.setPayer(payerOptional.get());
        expenseDetails.setAmountPaid(expenseRequest.getUserAmountPaid());
        expenseDetailsRepository.save(expenseDetails);
        logger.info("Expense details for expense ID {} saved", savedExpense.getId());
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
        logger.info("Group split updated for group ID {} and user ID {} with new split amount {}", group.getId(), user.getId(), newSplitAmount);
    }

    private record PaymentDetail(Long payer, Long payee, double amount, Expense expense) {
    }
}