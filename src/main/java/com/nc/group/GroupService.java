package com.nc.group;

import com.nc.exception.CreationException;
import com.nc.exception.DuplicateException;
import com.nc.exception.NotFoundException;
import com.nc.user.User;
import com.nc.user.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<GroupResponseDTO> getAllGroups() {
        logger.info("Fetching all groups");
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(GroupResponseDTO::fromGroup)
                .collect(Collectors.toList());
    }

    public GroupResponseDTO getGroupById(Long id) {
        logger.info("Fetching group with ID {}", id);
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Group with ID {} not found", id);
                    return new NotFoundException("Group with ID " + id + " not found");
                });
        return GroupResponseDTO.fromGroup(group);
    }

    public GroupResponseDTO saveOrUpdate(GroupRequest groupRequest) {
        logger.info("Saving or updating group with name {}", groupRequest.getGroupName());

        String groupName = groupRequest.getGroupName().trim();
        if (groupRepository.existsByGroupName(groupName)) {
            logger.error("Group with name {} already exists", groupName);
            throw new DuplicateException("Group with name " + groupName + " already exists");
        }

        List<User> users = groupRequest.getUserIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> {
                            logger.error("User with ID {} not found", userId);
                            return new NotFoundException("User with ID " + userId + " not found");
                        }))
                .toList();

        Group group = new Group();
        group.setGroupName(groupName);
        group.setUsers(users);

        try {
            Group savedGroup = groupRepository.save(group);
            logger.info("Group saved with ID {}", savedGroup.getId());
            return GroupResponseDTO.fromGroup(savedGroup);
        } catch (Exception e) {
            logger.error("Failed to create group: {}", e.getMessage());
            throw new CreationException("Failed to create new group " + e.getMessage());
        }
    }

    public void deleteGroup(Long id) {
        logger.info("Deleting group with ID {}", id);
        groupRepository.deleteById(id);
        logger.info("Group with ID {} deleted", id);
    }

    public List<User> addUser(Long groupId, List<User> users) {
        logger.info("Adding users to group with ID {}", groupId);
        return groupRepository.findById(groupId)
                .map(group -> {
                    group.getUsers().addAll(users);
                    groupRepository.save(group);
                    logger.info("Added {} users to group with ID {}", users.size(), groupId);
                    return group.getUsers();
                })
                .orElseThrow(() -> {
                    logger.error("Group with ID {} not found", groupId);
                    return new NotFoundException("Group with ID " + groupId + " not found");
                });
    }

    public List<Group> getGroupByName(String groupName) {
        logger.info("Fetching groups with name {}", groupName);
        return groupRepository.findByGroupName(groupName);
    }

    public Group update(Long id, GroupRequest groupRequest) {
        logger.info("Updating group with ID {}", id);

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Group with ID {} not found", id);
                    return new NotFoundException("Group with ID " + id + " not found");
                });

        group.setGroupName(groupRequest.getGroupName());
        List<User> users = groupRequest.getUserIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> {
                            logger.error("User with ID {} not found", userId);
                            return new NotFoundException("User with ID " + userId + " not found");
                        }))
                .toList();

        group.setUsers(users);
        Group updatedGroup = groupRepository.save(group);
        logger.info("Group with ID {} updated", updatedGroup.getId());
        return updatedGroup;
    }
}