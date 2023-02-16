package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.TypeReferences;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


// realization of Client model interface manages and manipulates the data
@RequiredArgsConstructor
@Getter
@Setter
public class ClientModelImpl implements ClientModel{
    private final AsyncRequestHandler handler ;
    private User myself;
    private Chat selectedChat;
    private List<Chat> myChats = new ArrayList<>();
    private List<Message> selectedChatMessages = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    @Override
    public boolean Authorize(String login, String password) {
        System.out.println("Authorize request: " + login);

        // действительно ли здесь надо на нулл проверять?
        if (myself != null) {
            System.out.println("Попытка ре-авторизации");
            return true;
        }
        if (authorizeRequest(login, password, "login")) {
            return true;
        }
        // если авторизация не успешна, то попробуем зарегистрировать, позже удалить
        return authorizeRequest(login, password, "register");
    }

    private boolean authorizeRequest(String login, String password, String request) {
        try {
            AuthResponse auth = handler.sendPOSTAsync(
                            "/auth/" + request,
                            AuthRequest.builder().login(login).password(password).build(),
                            false
                    )
                    .thenApply(AsyncRequestHandler.mapperOf(AuthResponse.class))
                    .get();

            setMyself(auth.getUser());
            handler.setAuthToken(auth.getToken());
        }
        catch (Exception e) {
            System.out.println(request + " failed\n" + e.getMessage());
            return false;
        }
        System.out.println(request + " OK");
        return true;
    }

    @Override
    public void updateMyChats() {
        if (myself == null) {
            return;
        }
        handler.sendGETAsync(String.format("/users/%d/chats", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfChat))
                .thenAccept(chats -> {
                    changeSupport.firePropertyChange("MyChatsUpdate", myChats, chats);
                    setMyChats(chats);
                });
    }

    @Override
    public void updateMessages() {
        if (selectedChat == null) {
            return;
        }
        handler.sendGETAsync(String.format("/chats/%d/messages", selectedChat.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(messages -> {
                    changeSupport.firePropertyChange("MessageUpdate", selectedChatMessages, messages);
                    setSelectedChatMessages(messages);
                });
    }

    @Override
    public void sendMessageToChat(String text) {
        if (selectedChat == null) {
            return;
        }
        handler.sendPOSTAsync(
                        String.format("/users/%d/chats/%d/message", myself.getId(), selectedChat.getId()),
                        Message.builder().text(text).build(),
                        true
                );
    }

    @Override
    public void sendMessageToUser(String text, User user) {
        handler.sendPOSTAsync(
                        String.format("/users/%d/message/users/%d", myself.getId(), user.getId()),
                        Message.builder().text(text).build(),
                        true
                );


    }

    @Override
    public void updateAllUsers() {
        handler.sendGETAsync("/users")
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfUser))
                .thenApply(userList -> {
                    userList.remove(myself);
                    return userList;
                })
                .thenAccept(userList -> {
                    changeSupport.firePropertyChange("AllUsers", allUsers, userList);
                    setAllUsers(userList);
                });
    }

    @Override
    public Chat createDialog(User user) {
        try {
            return handler.sendPOSTAsync(
                            "/chat?isPrivate=true",
                            List.of(myself.getId(), user.getId()),
                            true
                    )
                    .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                    .get();
        } catch (Exception e) {
            System.out.println("Fail to Create dialog");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }
}
