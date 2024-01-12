package com.ade.chatclient.api;

import com.ade.chatclient.domain.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UsersApi {
    CompletableFuture<List<User>> fetchAllUsersOfCompany(Long companyId);
}
