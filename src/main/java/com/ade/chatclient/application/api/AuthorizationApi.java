package com.ade.chatclient.application.api;

import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.RegisterData;

import java.util.concurrent.ExecutionException;

public interface AuthorizationApi {
    AuthResponse authorize(String login, String password) throws ExecutionException, InterruptedException;

    AuthRequest registerUser(RegisterData data) throws ExecutionException, InterruptedException;
}
