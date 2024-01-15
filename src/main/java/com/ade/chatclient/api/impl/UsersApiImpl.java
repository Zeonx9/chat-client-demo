package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.UsersApi;
import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.TypeReferences;
import com.ade.chatclient.domain.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsersApiImpl extends BaseRestApi implements UsersApi {

    public UsersApiImpl(AsyncRequestHandler handler) {
        super(handler);
    }

    @Override
    public CompletableFuture<List<User>> fetchAllUsersOfCompany(Long companyId) {
        return handler.sendGet(
                String.format("/company/%d/users", companyId),
                TypeReferences.ListOfUser
        );
    }
}
