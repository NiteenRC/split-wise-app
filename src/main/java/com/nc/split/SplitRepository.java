package com.nc.split;

import com.nc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SplitRepository extends JpaRepository<Split, Long> {
    Optional<Split> findByUser(User user);
}
