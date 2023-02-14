package com.ade.chatclient.model;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;

import java.beans.PropertyChangeListener;

// an interface that a model should implement, used to add flexibility to model structure
public interface ClientModel {

    /**
     * @return Авторизованного пользователя
     */
    User getMyself();

    /**
     * отправляет GET запрос на получение списка всех чатов авторизованного пользователя
     * присваивает полученные чаты в myChats
     */
    void updateMyChats();

    /**
     * присваивает selectedChat значение параметра
     * если чат в есть в списке чатов пользователя
     * @param chat чат, историю которого хотят получить
     */
    void setSelectedChat(Chat chat);

    /**
     *  отправляет GET запрос на историю, выбранного чата
     *  присваивает полученные сообщения в selectedChatMessages
     */
    void updateMessages();

    /**
     * отправляет POST запрос для регистрации/входа
     * @param login логин пользователя, который хочет авторизоваться
     * @return true - если авторизацция прошла успешно, иначе false
     */
    // method that authorize the user with given login(name)
    boolean Authorize(String login);

    /**
     * отправляет POST запрос с сообщением в selectedChat
     * @param text сообщение
     */
    void sendMessageToChat(String text);

    /**
     * отправляет POST запрос с сообщением в чат с выбранным пользователем
     * @param text сообщение
     * @param user пользователь, в чат которому надо отправить сообщение
     */
    void sendMessageToUser(String text, User user);

    /**
     * отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным
     * @param user выбранный пользователь
     */
    Chat createDialog(User user);

    void addListener(String eventName, PropertyChangeListener listener);

    void updateAllUsers();
}
