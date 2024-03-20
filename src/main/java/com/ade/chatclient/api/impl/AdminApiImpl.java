package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.AdminApi;
import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;

import java.util.concurrent.ExecutionException;

public class AdminApiImpl extends BaseRestApi implements AdminApi {
    public AdminApiImpl(AsyncRequestHandler handler) {
        super(handler);
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
