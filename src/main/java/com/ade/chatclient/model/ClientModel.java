package com.ade.chatclient.model;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

// an interface that a model should implement, used to add flexibility to model structure
public interface ClientModel {

    /**
     * добавляет слушателя за изменениями модели
     * @param eventName - названия события изменения
     * @param listener - методе-обработчик данного события
     */
    void addListener(String eventName, PropertyChangeListener listener);

    /**
     * отправляет POST запрос для регистрации/входа
     * @param login логин пользователя, который хочет авторизоваться
     * @return true - если авторизацция прошла успешно, иначе false
     */
    // method that authorize the user with given login(name)
    boolean Authorize(String login, String password);

    /**
     * @return Авторизованного пользователя
     */
    User getMyself();

    /**
     * присваивает selectedChat значение параметра
     * если чат в есть в списке чатов пользователя
     * @param chat чат, историю которого хотят получить
     */
    void selectChat(Chat chat);

    Chat getSelectedChat();

    /**
     * отправляет POST запрос с сообщением в selectedChat
     * @param text сообщение
     */
    void sendMessageToChat(String text);

    /**
     *  отправляет GET запрос на историю, выбранного чата
     *  присваивает полученные сообщения в selectedChatMessages
     */
    void fetchChatMessages();

    /**
     * отправляет GET запрос на получение списка всех чатов авторизованного пользователя
     * присваивает полученные чаты в myChats
     */
    void fetchChats();

    /**
     * Отправляет GET запрос на получение списка всех пользователей
     */
    void fetchUsers();

    /**
     * Отправляет GET запрос на получение undelivered_messages, то есть те, который есть на сервере
     * но еще не были получены пользователем
     */
    void fetchNewMessages();

    /**
     * отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным
     * @param user выбранный пользователь
     */
    Chat createDialogFromAllUsers(User user);

    void createDialogFromNewMessage(User user);

    Chat createGroupFromAllUsers(ArrayList<User> users);
}
