package com.ade.chatclient.api;

import com.ade.chatclient.dtos.AuthResponse;

import java.util.concurrent.ExecutionException;

public interface AuthorizationApi {
    AuthResponse authorize(String login, String password) throws ExecutionException, InterruptedException;

    void setHandlerAuthToken(String token);
}
