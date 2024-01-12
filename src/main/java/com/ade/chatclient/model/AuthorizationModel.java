package com.ade.chatclient.model;

public interface AuthorizationModel {
    /**
     * Отправляет POST запрос для входа
     *
     * @param login    логин, введенный пользователем
     * @param password пароль, введенный пользователем
     * @return true - если авторизация прошла успешно, иначе false
     */
    boolean authorize(String login, String password);

    /**
     * @return является ли авторизованный пользователь администратором
     */
    boolean isAdmin();
}
