package com.nc.balance;

import com.nc.exception.NotFoundException;
import com.nc.user.User;
import com.nc.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/balances")
public class BalanceController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{userId}")
    public String getBalanceSheet(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        double owes = 0.0;
        double owed = 0.0;

        StringBuilder balanceSheet = new StringBuilder();
        balanceSheet.append("Balance Sheet for ").append(user.getUsername()).append(":\n");

        List<Balance> balances = balanceService.getBalancesForUser(user);

        balanceSheet.append("Owes:\n");
        for (Balance balance : balances) {
            if (balance.getUser1().equals(user) && balance.getAmount() > 0) {
                balanceSheet.append("  - ").append(balance.getUser2().getUsername()).append(": ").append(balance.getAmount()).append("\n");
                owes += balance.getAmount();
            } else if (balance.getUser2().equals(user) && balance.getAmount() < 0) {
                balanceSheet.append("  - ").append(balance.getUser1().getUsername()).append(": ").append(Math.abs(balance.getAmount())).append("\n");
                owes += Math.abs(balance.getAmount());
            }
        }

        balanceSheet.append("Owed:\n");
        for (Balance balance : balances) {
            if (balance.getUser2().equals(user) && balance.getAmount() > 0) {
                balanceSheet.append("  - ").append(balance.getUser1().getUsername()).append(": ").append(balance.getAmount()).append("\n");
                owed += balance.getAmount();
            } else if (balance.getUser1().equals(user) && balance.getAmount() < 0) {
                balanceSheet.append("  - ").append(balance.getUser2().getUsername()).append(": ").append(Math.abs(balance.getAmount())).append("\n");
                owed += Math.abs(balance.getAmount());
            }
        }

        balanceSheet.append("Total Owes: ").append(owes).append("\n");
        balanceSheet.append("Total Owed: ").append(owed).append("\n");

        return balanceSheet.toString();
    }
}
