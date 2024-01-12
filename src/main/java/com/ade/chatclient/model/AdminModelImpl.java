package com.ade.chatclient.model;

import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;
import com.ade.chatclient.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdminModelImpl implements AdminModel {
    private final AdminRepository adminRepository;

    @Override
    public AuthRequest registerUser(RegisterData data) {
        return adminRepository.registerUser(data);
    }

    @Override
    public User getMyself() {
        return adminRepository.getMyself();
    }

    @Override
    public Company getCompany() {
        return adminRepository.getCompany();
    }

    @Override
    public void clearModel() {
        adminRepository.clear();
    }
}
