package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// realization of Client model interface manages and manipulates the data
@RequiredArgsConstructor
public class ClientModelManager implements ClientModel{
    private final RequestHandler handler ;
    @Getter
    private User myself;
    @Setter
    private Chat selectedChat;
    private List<Chat> myChats = new ArrayList<>();
    @Getter
    private List<Message> selectedChatMessages = new ArrayList<>();
    private List<User> users = new ArrayList<>();


    public void setMySelf(User user) {
        myself = user;
    }

    /**
     * обновляет чаты пользователя
     * @return все чаты пользователя
     */
    @Override
    public List<Chat> getMyChats() {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in");

        updateMyChats();

        return myChats;
    }

    /**
     * отправляет GET запрос на получение списка всех чатов авторизованного пользователя
     * присваивает полученные чаты в myChats
     */
    @Override
    public void updateMyChats() {
        myChats = handler.mapResponse(
                handler.GETRequest(String.format("/users/%d/chats", myself.getId())),
                RequestHandler.Types.ListOfChat
        );
    }

    /**
     * отправляет GET запрос на существование пользователя
     * @param login логин пользователя, который хочет авторизоваться
     * @return true - если авторизацция прошла успешно, иначе false
     */
    @Override
    public boolean Authorize(String login) {
        System.out.println("Authorize request: " + login);
        if (myself == null) {
            try {
                myself = handler.mapResponse(
                        handler.GETRequest("/user", Map.of("name", login)),
                        User.class
                );
            }
            catch (Exception e) {
                System.out.println("Authorization failed");
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * присваивает selectedChat значение параметра
     * если чат в есть в списке чатов пользователя
     * @param chat чат, историю которого хотят получить
     */
    @Override
    public void selectChat(Chat chat) {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in or you don't have chats");

        boolean hasSuchChat = false;
        for (Chat myChat : myChats) {
            if (myChat.getId().equals(chat.getId())) {
                hasSuchChat = true;
                break;
            }
        }
        if (!hasSuchChat) {
            System.out.println("User does not has a chat with id: " + chat.getId());
        }
        else setSelectedChat(chat);
    }

    /**
     *  отправляет GET запрос на историю, выбранного чата
     *  присваивает полученные сообщения в selectedChatMessages
     */
    @Override
    public void updateMessages() {
        selectedChatMessages = handler.mapResponse(
                handler.GETRequest(String.format("/chats/%d/messages", selectedChat.getId())),
                RequestHandler.Types.ListOfMessage
        );
    }

    /**
     * отправляет POST запрос с сообщением в selectedChat
     * @param text сообщение
     */
    @Override
    public void sendMessageToChat(String text) {
        handler.sendPOST(
                handler.POSTRequest(
                        String.format("/users/%d/chats/%d/message", myself.getId(), selectedChat.getId()),
                        makeBodyForMsgSending(text)
                )
        );
    }

    /**
     *
     * @param text сообщение
     * @return сообщение в нужной форме для отправки на сервер
     */
    private String makeBodyForMsgSending(String text) {
        return "{ \"text\": \"" + text + "\" }";
    }

    /**
     * инициализирует список всех пользователей, если он пуст
     * @return список всех пользователей
     */
    @Override
    public List<User> getAllUsers() {
        if (users.isEmpty()) {
            allUsers();
        }
        return users;
    }

    /**
     * отправляет GET запрос на получение списка всех пользователей
     * присваивает полученных пользователей в users
     */
    private void allUsers() {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in");

        users = handler.mapResponse(
                handler.GETRequest("/users"),
                RequestHandler.Types.ListOfUser
        );
    }

    /**
     * отправляет POST запрос с сообщением в чат с выбранным пользователем
     * @param text сообщение
     * @param user пользователь, в чат которому надо отправить сообщение
     */
    @Override
    public void sendMessageToUser(String text, User user) {
        handler.sendPOST(
                handler.POSTRequest(
                        String.format("/users/%d/message/users/%d", myself.getId(), user.getId()),
                        makeBodyForMsgSending(text)
                )
        );
    }

    /**
     * отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным
     * @param user выбранный пользователь
     */
    @Override
    public void createDialog(User user) {
        handler.sendPOST(
                handler.POSTRequest(
                        "/chat?isPrivate=true",
                        List.of(myself.getId(), user.getId())
                )
        );
    }

}
