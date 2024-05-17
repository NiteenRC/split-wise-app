package com.nc.group;

import com.nc.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

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

    public List<User> addUser(Long groupId, List<User> user) {
        Optional<Group> grp1 = groupRepository.findById(groupId);
        if (grp1.isPresent()) {
            grp1.get().getUsers().addAll(user);
            groupRepository.save(grp1.get());
        }
        return grp1.get().getUsers();
    }

    public List<Group> getGroupByName(String groupName) {
        return groupRepository.findByGroupName(groupName);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }
}
