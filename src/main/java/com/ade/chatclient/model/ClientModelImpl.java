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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Getter
@Setter
public class ClientModelImpl implements ClientModel{
    private final AsyncRequestHandler handler;
    private User myself;
    private Chat selectedChat;
    private List<Chat> myChats = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    @Override
    public boolean Authorize(String login, String password) {
        System.out.println("Authorize request: " + login);

        // действительно ли здесь надо на нулл проверять?
        // пока у нас нет кнопки для выхода не надо
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
                    // этот метод запускается только один раз сразу после входа
                    changeSupport.firePropertyChange("MyChatsUpdate", null, chats);
                    setMyChats(chats);
                });
    }

    @Override
    public void getMessages() {
        if (selectedChat == null) {
            return;
        }
        handler.sendGETAsync(String.format("/chats/%d/messages?userId=%d", selectedChat.getId(), myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(messages -> {
                    // только для обновления сообщений при выборе нового чата
                    changeSupport.firePropertyChange("MessageUpdate", null, messages);
                    selectedChat.setUnreadCount(0L);
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
                        true)
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(message -> changeSupport.firePropertyChange("newSelectedMessages", null, List.of(message)));
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
    public void updateMessages() {
        handler.sendGETAsync(String.format("/users/%d/undelivered_messages", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(this::acceptNewMessages);
    }

    private void acceptNewMessages(List<Message> newMessages) {
        Map<Boolean, List<Message>> splitBySelectedChat = newMessages.stream().collect(
                Collectors.partitioningBy(
                        message -> selectedChat != null && message.getChatId().equals(selectedChat.getId())
                )
        );
        if (!splitBySelectedChat.get(true).isEmpty()){
            changeSupport.firePropertyChange("newSelectedMessages", null, splitBySelectedChat.get(true));
        }
        if (!splitBySelectedChat.get(false).isEmpty()) {
            updateUnreadMessagesInChats(splitBySelectedChat.get(false));
        }
    }

    private void updateUnreadMessagesInChats(List<Message> messages) {
        Map<Long, Chat> chatById = myChats.stream()
                .collect(Collectors.toMap(Chat::getId, Function.identity()));
        Map <Boolean, List<Message>> split = messages.stream()
                .collect(Collectors.partitioningBy(mes -> chatById.containsKey(mes.getChatId())));

        split.get(true).forEach(message -> chatById.get(message.getChatId()).incrementUnreadCount());

        split.get(false).forEach(message -> createDialogFromNewMessage(message.getAuthor()));
    }


    private CompletableFuture<Chat> futureChatWith(User user) {
        return handler.sendPOSTAsync(
                        "/chat?isPrivate=true",
                        List.of(myself.getId(), user.getId()),
                        true
                )
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class));
    }

    private CompletableFuture<Chat> futureGroupWith(ArrayList<User> users) {
        return handler.sendPOSTAsync(
                        "/chat?isPrivate=false",
                        Stream.concat(users.stream().map(User::getId).toList().stream(), Stream.of(myself.getId())).toList(),
                        true
                )
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class));
    }

    @Override
    public Chat createGroupFromAllUsers(ArrayList<User> users) {
        System.out.println(Stream.concat(users.stream().map(User::getId).toList().stream(), Stream.of(myself.getId())).toList());
        try {
            Chat chat = futureGroupWith(users).get();
            if (!myChats.contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
                myChats.add(chat);
            }
            return chat;
        } catch (Exception e) {
            System.out.println("Fail to Create dialog");
            throw new RuntimeException(e);
        }
    }


    @Override
    public Chat createDialogFromAllUsers(User user) {
        try {
            Chat chat = futureChatWith(user).get();
            if (!myChats.contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
                myChats.add(chat);
            }
            return chat;
        } catch (Exception e) {
            System.out.println("Fail to Create dialog");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createDialogFromNewMessage(User user) {
        futureChatWith(user).thenAccept(chat -> {
            changeSupport.firePropertyChange("NewChatCreated", null, chat);
            myChats.add(chat);
        });
    }



    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }
}
