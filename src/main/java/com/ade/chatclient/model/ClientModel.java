package com.ade.chatclient.model;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

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
    void setSelectedChat(Chat chat);

    Chat getSelectedChat();

    /**
     *  отправляет GET запрос на историю, выбранного чата
     *  присваивает полученные сообщения в selectedChatMessages
     */
    void getMessages();

    /**
     * отправляет POST запрос с сообщением в selectedChat
     * @param text сообщение
     */
    void sendMessageToChat(String text);

    Chat createGroupFromAllUsers(ArrayList<User> users);

    /**
     * отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным
     * @param user выбранный пользователь
     */
    Chat createDialogFromAllUsers(User user);

    /**
     * отправляет GET запрос на получение списка всех чатов авторизованного пользователя
     * присваивает полученные чаты в myChats
     */
    void updateMyChats();

    //TODO доделать документацию

    void updateAllUsers();

    void updateMessages();

    List<User> getAllUsers();

    void createDialogFromNewMessage(User user);
}
