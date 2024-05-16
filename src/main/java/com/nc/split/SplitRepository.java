package com.nc.split;

import com.nc.group.Group;
import com.nc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SplitRepository extends JpaRepository<Split, Long> {
    Optional<Split> findByUser(User user);

    Optional<Split> findByUserAndGroup(User user, Group group);

    List<Split> findByGroup(Group group);

    Optional<Split> findByGroupAndUser(Group group, User user);
}
