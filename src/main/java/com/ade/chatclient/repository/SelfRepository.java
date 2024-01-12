package com.ade.chatclient.repository;

import com.ade.chatclient.api.SelfApi;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SelfRepository {
    private final SelfApi selfApi;

    @Setter
    @Getter
    private User myself;

    @Setter
    @Getter
    private Company company;

    public CompletableFuture<AuthResponse> changePassword(ChangePasswordRequest request) {
        return selfApi.changePassword(request);
    }

    public void clear() {
        myself = null;
        company = null;
    }

}
