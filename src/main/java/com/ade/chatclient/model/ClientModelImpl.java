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
import java.util.stream.Collectors;


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
    private List<Message> newSelectedChatMessages = new ArrayList<>();
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
                    changeSupport.firePropertyChange("MyChatsUpdate", myChats, chats);
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
                        true)
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(message -> {
                    selectedChatMessages.add(message);
                    changeSupport.firePropertyChange("sentMessage", null, message);
                });
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
    public void updateMessages() {
        handler.sendGETAsync(String.format("/users/%d/undelivered_messages", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(messages -> {
                    incomingMessages(messages);
                    updateUnreadMessagesInChats((messages));
                });

    }

    private void incomingMessages(List<Message> messages) {
        if (selectedChat == null) {
            return;
        }

        setNewSelectedChatMessages(messages.stream()
                .filter(message -> message.getChatId().equals(selectedChat.getId()))
                .toList()
        );

        changeSupport.firePropertyChange("incomingMessages", new ArrayList<>(), newSelectedChatMessages);

    }

    private void updateUnreadMessagesInChats(List<Message> messages) {
        Set<Long> idsOfChats= myChats.stream().map(Chat::getId).collect(Collectors.toSet());
        Map <Boolean, List<Message>> split = messages.stream()
                .collect(Collectors.partitioningBy(mes -> idsOfChats.contains(mes.getChatId())));

        split.get(true).forEach(message -> {
            for (Chat chat : myChats) {
                if (message.getChatId().equals(chat.getId())) {
                    chat.inc();
                }
            }
        });
//        todo событие для новых уведомлений
//        changeSupport.firePropertyChange("MyChatsUpdate", null, myChats);

        split.get(false).forEach(message -> createDialogFromNewMessage(message.getAuthor()));

    }

    @Override
    public Chat createDialogFromAllUsers(User user) {
        try {
            Chat chat = handler.sendPOSTAsync(
                            "/chat?isPrivate=true",
                            List.of(myself.getId(), user.getId()),
                            true
                    )
                    .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                    .get();
            if (!myChats.contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
            }
            return chat;
        } catch (Exception e) {
            System.out.println("Fail to Create dialog");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createDialogFromNewMessage(User user) {
            handler.sendPOSTAsync(
                            "/chat?isPrivate=true",
                            List.of(myself.getId(), user.getId()),
                            true
                    )
                    .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                    .thenAccept(chat -> {
                        changeSupport.firePropertyChange("NewChatCreated", null, chat);
                        myChats.add(chat);
                    });
    }

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }
}
