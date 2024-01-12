package com.ade.chatclient.repository;

import com.ade.chatclient.api.UsersApi;
import com.ade.chatclient.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsersRepository {
    private final UsersApi usersApi;
    @Setter
    private Long selfId;
    private List<User> allUsers = new ArrayList<>();

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

    public void clearUsers() {
        allUsers.clear();
    }

    public List<User> getUsers() {
        return allUsers;
    }

}
