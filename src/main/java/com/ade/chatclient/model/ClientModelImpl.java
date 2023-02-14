package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.TypeReferences;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.MessageDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


// realization of Client model interface manages and manipulates the data
@RequiredArgsConstructor
public class ClientModelImpl implements ClientModel{
    private final AsyncRequestHandler handler ;
    @Getter @Setter
    private User myself;
    @Getter @Setter
    private Chat selectedChat;
    @Setter
    private List<Chat> myChats = new ArrayList<>();
    @Getter @Setter
    private List<Message> selectedChatMessages = new ArrayList<>();
    @Setter
    private List<User> users = new ArrayList<>();

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private List<Message> lastSelectedChatMessages = new ArrayList<>();
    private List<Chat> lastMyChats = new ArrayList<>();

    @Override
    public boolean Authorize(String login) {
        System.out.println("Authorize request: " + login);

        //TODO добавить поле для ввода пароля
        var password = "Dasha";

        if (myself == null) {
            if (!authorizeRequest(login, password, "login")) {
                return authorizeRequest(login, password, "register");
            }
        }
        return true;
    }

    private boolean authorizeRequest(String login, String password, String request) {
        try {
            AuthResponse auth = handler.sendPOSTAsync(
                            "/auth/" + request,
                            AuthRequest.builder().login(login).password(password).build(),
                            false)
                    .thenApply(AsyncRequestHandler.mapperOf(AuthResponse.class))
                    .get();

            setMyself(auth.getUser());
            handler.setAuthToken(auth.getToken());
        }
        catch (Exception e) {
            System.out.println(request + " failed");
            System.out.println(e.getMessage());
            return false;
        }
        System.out.println(request + " OK");
        return true;
    }

    @Override
    public List<Chat> getMyChats() {
        if (selectedChat == null) return myChats;
//        if (myself == null)
//            throw new RuntimeException("attempt to get chats before log in");

        updateMyChats();
        return myChats;
    }

    @Override
    public void updateMyChats() {
        System.out.println(myself);
        if (myself == null) return;
        handler.sendGETAsync(String.format("/users/%d/chats", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfChat))
                .thenAccept(this::setMyChats);
        changeSupport.firePropertyChange("MyChatsUpdate", lastMyChats, myChats);
        lastMyChats = myChats;
    }

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
//        updateMessages();
    }

    @Override
    public void updateMessages() {
        if (selectedChat == null) return;
        handler.sendGETAsync(String.format("/chats/%d/messages", selectedChat.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(this::setSelectedChatMessages);
        changeSupport.firePropertyChange("MessageUpdate", lastSelectedChatMessages, selectedChatMessages);
        lastSelectedChatMessages = selectedChatMessages;
    }

    @Override
    public void sendMessageToChat(String text) {

        handler.sendPOSTAsync(
                String.format("/users/%d/chats/%d/message", myself.getId(), selectedChat.getId()),
                        MessageDto.builder().text(text).build(),
                true)
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(System.out::println);
    }

    @Override
    public List<User> getAllUsers() {
        if (users.isEmpty()) {
            allUsers();
        }
        return users;
    }

    private void allUsers() {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in");

        handler.sendGETAsync("/users")
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfUser))
                .thenAccept(this::setUsers);
    }

    @Override
    public void sendMessageToUser(String text, User user) {

        handler.sendPOSTAsync(
                String.format("/users/%d/message/users/%d", myself.getId(), user.getId()),
                        MessageDto.builder().text(text).build(),
                true)
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(System.out::println);
    }

    @Override
    public void createDialog(User user) {

        handler.sendPOSTAsync(
                "/chat?isPrivate=true",
                List.of(myself.getId(), user.getId()),
                true)
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                .thenAccept(System.out::println);
    }

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }

}
