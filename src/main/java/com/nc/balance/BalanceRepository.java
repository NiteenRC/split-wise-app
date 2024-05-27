package com.nc.balance;

import com.nc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByUser1OrUser2(User user1, User user2);

    Balance findByUser1AndUser2(User user1, User user2);
}
