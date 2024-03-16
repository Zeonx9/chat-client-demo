package com.ade.chatclient.api;

import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;

import java.util.concurrent.ExecutionException;

public interface AdminApi {
    AuthRequest registerUser(RegisterData data) throws ExecutionException, InterruptedException;
}
