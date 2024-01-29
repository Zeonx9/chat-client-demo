package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.SelfApi;
import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.ChangePasswordRequest;

import java.util.concurrent.CompletableFuture;

public class SelfApiImpl extends BaseRestApi implements SelfApi {
    public SelfApiImpl(AsyncRequestHandler handler) {
        super(handler);
    }

    @Override
    public CompletableFuture<AuthResponse> changePassword(ChangePasswordRequest request) {
        return handler.sendPut("/auth/user/password", request, AuthResponse.class);
    }

    @Override
    public CompletableFuture<User> changeUserInfo(User request) {
        return handler.sendPut(String.format("/user/%d", request.getId()), request, User.class);
    }


}
