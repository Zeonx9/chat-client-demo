package com.ade.chatclient.repository;

import com.ade.chatclient.domain.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UsersRepository extends Searchable<User> {
    void setSelfId(Long selfId);

    CompletableFuture<List<User>> fetchUsers(Long companyId);

    void clearUsers();

    List<User> getUsers();

    User updateOnlineStatus(long userId, boolean connect);
}
