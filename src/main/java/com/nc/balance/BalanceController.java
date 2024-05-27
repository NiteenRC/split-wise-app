package com.nc.balance;

import com.nc.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balances")
public class BalanceController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{userId}")
    public BalanceSheet getBalanceSheet(@PathVariable Long userId) {
        return balanceService.getBalanceSheetForUser(userId);
    }
}
