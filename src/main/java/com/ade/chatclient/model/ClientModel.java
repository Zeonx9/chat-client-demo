package com.ade.chatclient.model;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.dtos.GroupRequest;

import java.beans.PropertyChangeListener;
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
     * @return true - если авторизация прошла успешно, иначе false
     */
    // method that authorize the user with given login(name)
    boolean authorize(String login, String password);

    /**
     * @return Авторизованного пользователя
     */
    User getMyself();

    void clearModel();

    /**
     * присваивает selectedChat значение параметра
     * если чат в есть в списке чатов пользователя
     * @param chat чат, историю которого хотят получить
     */
    void setSelectChat(Chat chat);

    Chat getSelectedChat();

    List<User> getAllUsers();
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
     * Но еще не были получены пользователем
     */
    void fetchNewMessages();

    /**
     * отправляет на сервер запрос на изменение пароля о своем результате сообщает через
     * лисенер "passwordChangeResponded", который надо подключить к модели
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным
     * @param user выбранный пользователь
     */
    Chat createDialogFromAllUsers(User user);

    /**
     * отправляет POST запрос на создание группового чата между авторизованным и выбранными пользователями
     * @param groupRequest содержит информацию для создания чата
     */
    void createGroupChat(GroupRequest groupRequest);

    List<Chat> searchChat(String request);

    List<User> searchUser(String request);
    Company getCompany();

    List<Chat> getMyChats();
    void getMyChatsAfterSearching();
}
