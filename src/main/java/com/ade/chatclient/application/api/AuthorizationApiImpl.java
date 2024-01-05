package com.ade.chatclient.application.api;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.RegisterData;

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
    public AuthRequest registerUser(RegisterData data) throws ExecutionException, InterruptedException {
        return handler.sendPost(
                "/auth/register",
                data,
                AuthRequest.class,
                true
        ).get();
    }
}
