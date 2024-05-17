package com.nc.payment;

import com.nc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByExpenseExpenseName(String expenseName);

    List<Payment> findByPayerOrPayee(User payerId, User payeeId);

    List<Payment> findByPayerInOrPayeeIn(List<User> users, List<User> users1);
}
