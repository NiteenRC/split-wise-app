package com.nc.group;

import com.nc.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group saveOrUpdateGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public List<User> addUser(Long groupId, List<User> users) {
        return groupRepository.findById(groupId)
                .map(group -> {
                    group.getUsers().addAll(users);
                    groupRepository.save(group);
                    return group.getUsers();
                })
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public List<Group> getGroupByName(String groupName) {
        return groupRepository.findByGroupName(groupName);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }
}