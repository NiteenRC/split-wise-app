package com.nc.group;

import com.nc.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupName(String groupName);

    //List<User> findAllUsersById(Long groupId);

    @Query("SELECT u.users FROM Group u WHERE u.id = :groupId")
    List<User> findAllUsersById(@Param("groupId") Long groupId);

    boolean existsByGroupName(String name);

    @Query(value = "SELECT g.id FROM `group` g " +
            "JOIN group_users gu ON g.id = gu.group_id " +
            "JOIN user u ON u.id = gu.user_id " +
            "WHERE u.username = :username", nativeQuery = true)
    List<Long> findGroupIdsByUserId(@Param("username") String username);
}
