package com.ade.chatclient.api;

import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.ChangePasswordRequest;

import java.util.concurrent.CompletableFuture;

public interface SelfApi {
    CompletableFuture<AuthResponse> changePassword(ChangePasswordRequest request);

    CompletableFuture<User> changeUserInfo(User request);
}
