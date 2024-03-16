package com.ade.chatclient.repository;

import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.ChangePasswordRequest;

import java.util.concurrent.CompletableFuture;

public interface SelfRepository {
    CompletableFuture<AuthResponse> changePassword(ChangePasswordRequest request);

    void clear();

    void setMyself(User myself);

    User getMyself();

    void setCompany(Company company);

    Company getCompany();
}
