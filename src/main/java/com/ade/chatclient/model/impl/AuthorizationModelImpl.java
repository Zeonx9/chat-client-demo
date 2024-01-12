package com.ade.chatclient.model.impl;

import com.ade.chatclient.api.AuthorizationApi;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.model.AuthorizationModel;
import com.ade.chatclient.repository.AdminRepository;
import com.ade.chatclient.repository.SelfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationModelImpl implements AuthorizationModel {
    private final AuthorizationApi authApi;
    private final SelfRepository selfRepository;
    private final AdminRepository adminRepository;
    private boolean isAdmin;

    @Override
    public boolean authorize(String login, String password) {
        try {
            AuthResponse auth = authApi.authorize(login, password);
            isAdmin = auth.isAdmin();

            if (isAdmin) {
                adminRepository.setMyself(auth.getUser());
                adminRepository.setCompany(auth.getCompany());
            } else {
                selfRepository.setMyself(auth.getUser());
                selfRepository.setCompany(auth.getCompany());
            }
            authApi.setHandlerAuthToken(auth.getToken());
        } catch (Exception e) {
            log.error("login failed", e);
            return false;
        }
        log.info("authorized successfully");
        return true;
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }
}
