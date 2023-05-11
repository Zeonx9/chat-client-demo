package com.ade.chatclient.application;

import lombok.AllArgsConstructor;

import java.util.function.Function;


/**
 * Перечисление, которое содержит константы описывающие View или Pane в приложении.
 * Каждая константа описывает свой метод инициализации, в который передаются необходимые параметры
 */
@AllArgsConstructor
public enum Views {
    LOG_IN_VIEW("log-in-view", ViewModelProvider::getLogInViewModel),
    ALL_USERS_VIEW("all-users-view", ViewModelProvider::getAllUsersViewModel),
    ALL_CHATS_VIEW("all-chats-view", ViewModelProvider::getAllChatsViewModel),
    CHAT_PAGE_VIEW("chat-page-view", ViewModelProvider::getChatPageViewModel),
    USER_PROFILE_VIEW("user-profile-view", ViewModelProvider::getUserProfileViewModel),
    ADMIN_VIEW("admin-view", ViewModelProvider::getAdminViewModel);
    /**
     * Содержит имя связанного fxml файла без расширения ".fxml"
     */
    final String fxmlFileName;
    final Function<ViewModelProvider, ? extends AbstractViewModel<?>> viewModelGetter;
}
