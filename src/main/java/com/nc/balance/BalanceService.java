package com.nc.balance;

import com.nc.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    public List<Balance> getBalancesForUser(User user) {
        return balanceRepository.findByUser1OrUser2(user, user);
    }

    public Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    public Balance findByUsers(User user1, User user2) {
        return balanceRepository.findByUser1AndUser2(user1, user2);
    }
}
