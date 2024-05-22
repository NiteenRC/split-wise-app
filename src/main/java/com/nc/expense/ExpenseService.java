package com.nc.expense;

import com.nc.exception.CreationException;
import com.nc.exception.DuplicateException;
import com.nc.exception.NotFoundException;
import com.nc.expenseDetails.ExpenseDetails;
import com.nc.expenseDetails.ExpenseDetailsRepository;
import com.nc.group.Group;
import com.nc.group.GroupRepository;
import com.nc.payment.Payment;
import com.nc.payment.PaymentService;
import com.nc.split.Split;
import com.nc.split.SplitRepository;
import com.nc.split.SplitService;
import com.nc.user.User;
import com.nc.user.UserRepository;
import com.nc.utility.UserContext;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .map(ExpenseDTO::convertToDto)
                .toList();
    }

    public List<ExpenseDTO> saveOrUpdate(ExpenseRequest expenseRequest) {
        User user = validateAndGetUser();
        expenseRequest.setPayer(user.getId());
        List<Expense> expenses = new ArrayList<>();

        switch (expenseRequest.getSplitType()) {
            case EQUAL -> {
                if (expenseRequest.getUserAmountPaid() != expenseRequest.getExpenseAmount()) {
                    logger.error("Amount paid must equal to expense amount. User needs to pay: {}", expenseRequest.getExpenseAmount());
                    throw new CreationException("Amount paid must equal to expense amount. User needs to pay: " + expenseRequest.getExpenseAmount());
                }
                Expense expense = saveExpense(expenseRequest);
                expenses.add(expense);
            }
            case NON_EQUAL -> {
                Expense expense = handleNonEqualSplit(expenseRequest);
                expenses.add(expense);
            }
            default -> {
                logger.error("Invalid SplitType {}", expenseRequest.getSplitType());
                throw new RuntimeException("SplitType not valid");
            }
        }

        Expense expense = expenses.get(0);
        splitExpenseAmongUsers(expenseRequest, expense);
        saveExpenseDetails(expenseRequest, expense);

        return expenses.stream()
                .map(ExpenseDTO::convertToDto)
                .toList();
    }

    private User validateAndGetUser() {
        String username = UserContext.currentUsername();
        logger.info("Validating user with username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User with name {} not found", username);
                    return new NotFoundException("User with name " + username + " not found");
                });
    }

    private Expense saveExpense(ExpenseRequest expenseRequest) {
        validateExpenseName(expenseRequest.getExpenseName());
        Group group = validateAndGetGroup(expenseRequest.getGroupId(), expenseRequest.getSplitBetweenUserIds(), expenseRequest.getPayer());
        Expense expenseObject = createExpenseObject(expenseRequest, group);
        return saveExpense(expenseObject);
    }

    private Expense handleNonEqualSplit(ExpenseRequest expenseRequest) {
        logger.info("Handling non-equal split for expense: {}", expenseRequest.getExpenseName());

        Optional<Expense> optionalExpense = expenseRepository
                .findByExpenseNameAndSplitType(expenseRequest.getExpenseName(), expenseRequest.getSplitType());

        if (optionalExpense.isEmpty()) {
            logger.info("Expense with name {} and split type {} not found, creating a new one", expenseRequest.getExpenseName(), expenseRequest.getSplitType());
            return saveExpense(expenseRequest);
        } else {
            Expense expense = optionalExpense.get();
            List<ExpenseDetails> expenseDetails = expenseDetailsRepository.findByExpense(expense);
            double totalAmountPaid = expenseDetails.stream().mapToDouble(ExpenseDetails::getAmountPaid).sum();

            logger.info("Total amount paid so far for expense {}: {}", expenseRequest.getExpenseName(), totalAmountPaid);

            if (totalAmountPaid == expense.getExpenseAmount()) {
                logger.error("Total expense amount already paid");
                throw new CreationException("Total expense amount already paid");
            } else if (totalAmountPaid + expenseRequest.getUserAmountPaid() > expense.getExpenseAmount()) {
                logger.error("Amount paid exceeds the expense amount. User needs to pay: {}", expense.getExpenseAmount() - totalAmountPaid);
                throw new CreationException("Amount paid exceeds the expense amount. User needs to pay: " + (expense.getExpenseAmount() - totalAmountPaid));
            }
            return expense;
        }
    }

    private void validateExpenseName(String expenseName) {
        if (expenseRepository.existsByExpenseName(expenseName)) {
            logger.error("Expense with name {} already exists", expenseName);
            throw new DuplicateException("Expense with name " + expenseName + " already exists");
        }
    }

    private Group validateAndGetGroup(Long groupId, List<Long> splitBetweenUserIds, Long payer) {
        logger.info("Validating group with ID: {}", groupId);
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        if (groupOptional.isEmpty()) {
            logger.error("Group with ID {} not found", groupId);
            throw new NotFoundException("Group with ID " + groupId + " not found");
        }
        Group group = groupOptional.get();
        validateGroupUsers(group, splitBetweenUserIds, payer);
        return group;
    }

    private void validateGroupUsers(Group group, List<Long> splitBetweenUserIds, Long payer) {
        List<Long> groupUserIds = group.getUsers().stream().map(User::getId).toList();
        List<Long> invalidUserIds = splitBetweenUserIds.stream()
                .filter(userId -> !groupUserIds.contains(userId))
                .toList();

        if (!invalidUserIds.isEmpty()) {
            logger.error("Users with IDs {} not part of the group ID {}", invalidUserIds, group.getId());
            throw new NotFoundException("Users with IDs " + invalidUserIds + " not part of the group with ID " + group.getId());
        }

        if (!groupUserIds.contains(payer)) {
            logger.error("Payer with ID {} not part of the group ID {}", payer, group.getId());
            throw new NotFoundException("Payer with ID " + payer + " not part of the group with ID " + group.getId());
        }
    }

    private Expense createExpenseObject(ExpenseRequest expenseRequest, Group group) {
        Expense expense = new Expense();
        expense.setExpenseName(expenseRequest.getExpenseName());
        expense.setExpenseAmount(expenseRequest.getExpenseAmount());
        expense.setExpenseType(expenseRequest.getExpenseType());
        expense.setSplitType(expenseRequest.getSplitType());
        expense.setGroup(group);
        logger.info("Created expense object for expense: {}", expenseRequest.getExpenseName());
        return expense;
    }

    private Expense saveExpense(Expense expense) {
        try {
            Expense savedExpense = expenseRepository.save(expense);
            logger.info("Expense with ID {} saved", savedExpense.getId());
            return savedExpense;
        } catch (Exception e) {
            logger.error("Failed to create Expense: {}", e.getMessage());
            throw new CreationException("Failed to create new Expense " + e.getMessage());
        }
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
        payment.setGroup(expense.getGroup());
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