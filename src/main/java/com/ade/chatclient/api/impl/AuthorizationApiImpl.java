package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.AuthorizationApi;
import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;

import java.util.concurrent.ExecutionException;

public class AuthorizationApiImpl extends BaseRestApi implements AuthorizationApi {
    public AuthorizationApiImpl(AsyncRequestHandler handler) {
        super(handler);
    }

    @Override
    public AuthResponse authorize(String login, String password) throws ExecutionException, InterruptedException {
        return handler.sendPost(
                "/auth/login",
                AuthRequest.builder().login(login).password(password).build(),
                AuthResponse.class, false
        ).get();
    }

    @Override
    public void setHandlerAuthToken(String token) {
        handler.setAuthToken(token);
    }
}
