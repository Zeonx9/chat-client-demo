package com.ade.chatclient.repository.impl;

import com.ade.chatclient.api.AdminApi;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;
import com.ade.chatclient.repository.AdminRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {
    private final AdminApi adminApi;

    @Getter
    @Setter
    private User myself;
    @Getter
    @Setter
    private Company company;

    @Override
    public AuthRequest registerUser(RegisterData data) {
        try {
            return adminApi.registerUser(data);
        } catch (Exception e) {
            log.error("user registration failed", e);
            return null;
        }
    }

    @Override
    public void clear() {
        myself = null;
        company = null;
    }
}
