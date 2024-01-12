package com.ade.chatclient.repository;

import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;

public interface AdminRepository {
    AuthRequest registerUser(RegisterData data);

    void clear();

    User getMyself();

    void setMyself(User myself);

    Company getCompany();

    void setCompany(Company company);
}
