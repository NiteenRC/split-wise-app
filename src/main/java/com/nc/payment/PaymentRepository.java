package com.nc.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT e.expense_name AS expenseName, " +
            "       SUM(p.amount) AS totalAmount, " +
            "       GROUP_CONCAT(u.username SEPARATOR ',') AS userNames " +
            "FROM payment p " +
            "JOIN expense e ON p.expense_id = e.id " +
            "JOIN user u ON p.payee_id = u.id " +
            "GROUP BY e.expense_name", nativeQuery = true)
    List<PaymentDTO> findExpenseSummaries();

    List<Payment> findAllByGroupIdIn(List<Long> list);

    @Query(value = "SELECT p.* FROM payment p " +
            "JOIN `group` g ON p.group_id = g.id " +
            "JOIN group_users gu ON g.id = gu.group_id " +
            "JOIN user u ON u.id = gu.user_id " +
            "WHERE u.username = :username", nativeQuery = true)
    List<Payment> findAllPaymentsByUserUsername(@Param("username") String username);
}
