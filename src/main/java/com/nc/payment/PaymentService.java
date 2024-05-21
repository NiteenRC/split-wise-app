package com.nc.payment;

import com.nc.group.GroupRepository;
import com.nc.utility.UserContext;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;

    public List<Payment> getAllTransactions() {
        logger.info("Fetching all transactions");
        return paymentRepository.findAll();
    }

    public Payment getTransactionById(Long id) {
        logger.info("Fetching transaction with ID {}", id);
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment saveOrUpdateTransaction(Payment payment) {
        logger.info("Saving/updating transaction for payment with amount {}", payment.getAmount());
        return paymentRepository.save(payment);
    }

    public void deleteTransaction(Long id) {
        logger.info("Deleting transaction with ID {}", id);
        paymentRepository.deleteById(id);
    }

    public List<PaymentSummary> getPaymentSummaries() {
        logger.info("Generating payment summaries");
        String username = UserContext.currentUsername();
        List<Payment> payments = paymentRepository.findAllPaymentsByUserUsername(username);

        Map<String, Map<String, Double>> totalAmountsByExpenseAndUser = calculateTotalAmountsByExpenseAndUser(payments);

        return totalAmountsByExpenseAndUser.entrySet().stream()
                .map(entry -> {
                    String expenseName = entry.getKey();
                    List<PaymentDTO> users = entry.getValue().entrySet().stream()
                            .map(userEntry -> new PaymentDTO(userEntry.getKey(), userEntry.getValue()))
                            .toList();
                    return new PaymentSummary(expenseName, users);
                })
                .toList();
    }

    private Map<String, Map<String, Double>> calculateTotalAmountsByExpenseAndUser(List<Payment> payments) {
        return payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getExpense().getExpenseName(), // Group by expense name
                        Collectors.groupingBy( // Further group by username within each expense name group
                                payment -> payment.getPayee().getUsername(),
                                Collectors.summingDouble(Payment::getAmount) // Calculate sum of amounts for each user
                        )
                ));
    }
}