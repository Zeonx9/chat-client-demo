package com.ade.chatclient.repository.impl;

import com.ade.chatclient.api.UsersApi;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsersRepositoryImpl implements UsersRepository {
    private final UsersApi usersApi;
    @Setter
    private Long selfId;
    private List<User> allUsers = new ArrayList<>();

    @Override
    public CompletableFuture<List<User>> fetchUsers(Long companyId) {
        if (allUsers.isEmpty()) {
            return usersApi.fetchAllUsersOfCompany(companyId).thenApply(userList -> {
                allUsers = userList
                        .stream()
                        .filter(user -> !Objects.equals(user.getId(), selfId))
                        .collect(Collectors.toList());
                return allUsers;
            });
        } else {
            return CompletableFuture.completedFuture(allUsers);
        }
    }

    @Override
    public void clearUsers() {
        selfId = null;
        allUsers.clear();
    }

    @Override
    public List<User> getUsers() {
        return allUsers;
    }

    @Override
    public List<User> search(String request) {
        return allUsers.stream().filter(user -> byUserName(user, processingSearchString(request))).toList();
    }

    @Override
    public User updateOnlineStatus(long userId, boolean connect) {
        for (User user : allUsers) {
            if (user.getId() == userId) {
                user.setIsOnline(connect);
                return user;
            }
        }
        return null;
    }
}
