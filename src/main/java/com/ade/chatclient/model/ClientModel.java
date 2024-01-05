package com.ade.chatclient.model;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.dtos.RegisterData;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * интерфейс, который должна реализовывать модель, используемый для придания гибкости структуре модели
 */
public interface ClientModel {

    /**
     * Добавляет слушателя за изменениями модели
     * @param eventName - названия события изменения
     * @param listener - методе-обработчик данного события
     */
    void addListener(String eventName, PropertyChangeListener listener);

    /**
     * Отправляет POST запрос для входа
     * @param login логин, введенный пользователем
     * @param password пароль, введенный пользователем
     * @return true - если авторизация прошла успешно, иначе false
     */
    boolean authorize(String login, String password);

    /**
     * Отправляет POST запрос на регистрацию пользователя
     * @param data данные необходимые для регистрации пользователя
     * @return данные зарегистрированного пользователя, если регистрация прошла успешно, иначе null
     */
    AuthRequest registerUser(RegisterData data);

    /**
     * @return Авторизованного пользователя
     */
    User getMyself();

    /**
     * Очищает данные модели при log out
     */
    void clearModel();

    /**
     * @return является ли авторизованный пользователь администратором
     */
    boolean isAdmin();

    /**
     * Присваивает selectedChat значение параметра. Вызывает функцию fetchChatMessages,
     * которая отправляет GET запрос на получении истории выбранного чата и обнуляет счетчик непрочитанных сообщений на selectedChat.
     * О полученных сообщениях сообщает через лисенер "gotMessages" и обновляет счетчик непрочитанных сообщений на чате через лисенер "selectedChatModified".
     * @param chat чат, историю которого хотят получить
     */
    void setSelectChat(Chat chat);

    /**
     * @return выбранный чат
     */
    Chat getSelectedChat();

    /**
     * После завершения поиска юзеров, возвращает исходное значение всех пользователей через лисенер "AllUsers"
     */
    void getAllUsersAfterSearching();

    /**
     * @return список всех пользователей компании авторизованного пользователя
     */
    List<User> getAllUsers();

    /**
     * Отправляет POST запрос с сообщением отправленным в выбранный чат и сортирует список чатов пользователя.
     * Уведомляет об отправленном сообщении через лисенер "newMessagesInSelected"
     * и перемещает selectedChat в начало списка с помощью лисенера "chatReceivedMessages"
     * @param text текст сообщения
     */
    void sendMessageToChat(String text);

    /**
     * Отправляет GET запрос на получение списка всех чатов авторизованного пользователя и уведомляет об изменениях через лисенер "gotChats"
     */
    void fetchChats();

    /**
     * Отправляет GET запрос на получение списка всех пользователей, уведомляет об изменениях через лисенер "AllUsers",
     * присваивает полученное значение в allUsers
     */
    void fetchUsers();

    /**
     * Отправляет GET запрос на получение undelivered_messages, то есть те, которые есть на сервере,
     * но еще не были получены пользователем. Полученные сообщения добавляет либо в открытый чат,
     * используя лисенеры "newMessagesInSelected" и "chatReceivedMessages",
     * либо увеличивает счетчик непрочитанных сообщений в чатах, куда они были отправлены, используя лисенер "chatReceivedMessages",
     * который перемещает чат в начало списка.
     * Если чата для принятого сообщения нет, то запрашивает чат и оповещает о полученном чате через лисенер "NewChatCreated", который добавляет новый чат в начало списка чатов.
     */
    void fetchNewMessages();

    /**
     * Отправляет PUT запрос на изменение пароля и о результате сообщает через
     * лисенер "passwordChangeResponded"
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * Отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным.
     * Если такой чат уже создан, то возвращает уже существующий. О результате уведомляет через лисенер "NewChatCreated"
     * @param user выбранный пользователь
     * @return созданный или открытый чат
     */
    Chat createDialogFromAllUsers(User user);

    /**
     * Отправляет POST запрос на создание группового чата между авторизованным и выбранными пользователями.
     * О результате уведомляет через лисенер "NewChatCreated"
     * @param groupRequest содержит информацию для создания чата
     */
    void createGroupChat(GroupRequest groupRequest);

    /**
     * Выполняет поиск чатов соответствующих request.
     * Поиск выполняется по логину, имени, фамилии юзера или по названию чата.
     * @param request текстовый запрос
     * @return список чатов удовлетворяющих запросу
     */
    List<Chat> searchChat(String request);

    /**
     * Выполняет поиск пользователей соответствующих request.
     * Поиск выполняется по логину, имени или фамилии.
     * @param request текстовый запрос
     * @return список пользователей удовлетворяющих запросу
     */
    List<User> searchUser(String request);

    /**
     * @return компанию авторизованного пользователя
     */
    Company getCompany();

    /**
     * После завершения поиска чатов, возвращает исходное значение всех чатов через лисенер "gotChats"
     */
    void getMyChatsAfterSearching();

    void startWebSocketConnection();

    void stopWebSocketConnection();
}
