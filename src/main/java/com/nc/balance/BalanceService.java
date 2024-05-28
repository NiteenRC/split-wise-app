package com.nc.balance;

import com.nc.exception.NotFoundException;
import com.nc.user.User;
import com.nc.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BalanceService {
    private BalanceRepository balanceRepository;
    private UserRepository userRepository;

    public List<Balance> getBalancesForUser(User user) {
        return balanceRepository.findByUser1OrUser2(user, user);
    }

    public Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    public BalanceSheet getBalanceSheetForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        List<Balance> balances = getBalancesForUser(user);

        List<String> owes = new ArrayList<>();
        List<String> owed = new ArrayList<>();
        double totalOwes = calculateBalances(balances, user, true, owes);
        double totalOwed = calculateBalances(balances, user, false, owed);

        return new BalanceSheet(user.getUsername(), owes, owed, totalOwes, totalOwed);
    }

    private double calculateBalances(List<Balance> balances, User user, boolean isOwing, List<String> result) {
        double total = 0.0;
        for (Balance balance : balances) {
            if (isOwing) {
                if (balance.getUser1().equals(user) && balance.getAmount() > 0) {
                    result.add(balance.getUser2().getUsername() + ": " + balance.getAmount());
                    total += balance.getAmount();
                } else if (balance.getUser2().equals(user) && balance.getAmount() < 0) {
                    result.add(balance.getUser1().getUsername() + ": " + Math.abs(balance.getAmount()));
                    total += Math.abs(balance.getAmount());
                }
            } else {
                if (balance.getUser2().equals(user) && balance.getAmount() > 0) {
                    result.add(balance.getUser1().getUsername() + ": " + balance.getAmount());
                    total += balance.getAmount();
                } else if (balance.getUser1().equals(user) && balance.getAmount() < 0) {
                    result.add(balance.getUser2().getUsername() + ": " + Math.abs(balance.getAmount()));
                    total += Math.abs(balance.getAmount());
                }
            }
        }
        return total;
    }

    public Balance findByUsers(User user1, User user2) {
        return balanceRepository.findByUser1AndUser2(user1, user2);
    }
}
