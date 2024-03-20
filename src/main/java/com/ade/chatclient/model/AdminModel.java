package com.ade.chatclient.model;

import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.RegisterData;


public interface AdminModel {
    /**
     * Отправляет POST запрос на регистрацию пользователя
     *
     * @param data данные необходимые для регистрации пользователя
     * @return данные зарегистрированного пользователя, если регистрация прошла успешно, иначе null
     */
    AuthRequest registerUser(RegisterData data);

    User getMyself();

    Company getCompany();

    /**
     * Очищает данные модели при log out
     */
    void clearModel();
}
