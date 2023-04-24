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
    private long unreadChatCounter;

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
    public void fetchChats() {
        if (myself == null) {
            return;
        }
        handler.sendGETAsync(String.format("/users/%d/chats", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfChat))
                .thenAccept(chats -> {
                    changeSupport.firePropertyChange("gotChats", null, chats);
                    setMyChats(chats);
                    updateUnreadChatCounter();
                });
    }

    private void updateUnreadChatCounter() {
        unreadChatCounter = myChats.stream().filter(Chat::isUnreadChat).count();
        System.out.println("New messages at login " + unreadChatCounter);
        changeSupport.firePropertyChange("UnreadChats", null, unreadChatCounter);
    }

    private void decrementChatCounter(Chat chat) {
        if (chat.isUnreadChat()) {
            System.out.println("open chat / counter" + chat);
            changeSupport.firePropertyChange("UnreadChats", null, unreadChatCounter--);
        }
    }

    private void incrementUnreadChatCounter(Chat chat) {
        if (!chat.isUnreadChat()) {
            System.out.println("new mes / counter" + chat);
            changeSupport.firePropertyChange("UnreadChats", null, unreadChatCounter++);
        }
    }

    @Override
    public void fetchChatMessages() {
        if (selectedChat == null) {
            return;
        }
        decrementChatCounter(selectedChat);
        handler.sendGETAsync(
                    String.format("/chats/%d/messages", selectedChat.getId()),
                    Map.of("userId", myself.getId().toString())
                )
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(messages -> {
                    System.out.println("fetching messages for chat " + selectedChat.getId() + "...");
                    changeSupport.firePropertyChange("gotMessages", null, messages);
                    selectedChat.setUnreadCount(0);
                });
    }

    @Override
    public void selectChat(Chat chat) {
        selectedChat = chat;
        fetchChatMessages();
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
                )
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(message -> {
                            selectedChat.setLastMessage(message);
                            changeSupport.firePropertyChange("newMessagesInSelected", null, List.of(message));
                            changeSupport.firePropertyChange("chatReceivedMessages", null, selectedChat);
                });
    }

    @Override
    public void fetchUsers() {
        //TODO change to company/users
        handler.sendGETAsync("/users")
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfUser))
                .thenApply(userList -> {
                    userList.remove(myself);
                    return userList;
                })
                .thenAccept(userList -> {
                    setAllUsers(userList);
                    changeSupport.firePropertyChange("AllUsers", null, userList);
                });
    }

    @Override
    public void fetchNewMessages() {
        handler.sendGETAsync(String.format("/users/%d/undelivered_messages", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(this::acceptNewMessages);
    }

    private void acceptNewMessages(List<Message> newMessages) {
        Map<Boolean, List<Message>> msgInSelectedChat = newMessages.stream().collect(Collectors.partitioningBy(
                message -> selectedChat != null && message.getChatId().equals(selectedChat.getId())
        ));
        if (!msgInSelectedChat.get(true).isEmpty()){
            changeSupport.firePropertyChange("newMessagesInSelected", null, msgInSelectedChat.get(true));
            changeSupport.firePropertyChange("chatReceivedMessages", null, selectedChat);
        }
        if (!msgInSelectedChat.get(false).isEmpty()) {
            processMessagesInOtherChats(msgInSelectedChat.get(false));
        }
    }

    private void processMessagesInOtherChats(List<Message> messages) {
        Map<Long, Chat> chatById = myChats.stream()
                .collect(Collectors.toMap(Chat::getId, Function.identity()));
        Map <Boolean, List<Message>> msgInExistingChat = messages.stream()
                .collect(Collectors.partitioningBy(mes -> chatById.containsKey(mes.getChatId())));

        for (Message msg : msgInExistingChat.get(true)) {
            Chat chatOfMessage = chatById.get(msg.getChatId());
            chatOfMessage.incrementUnreadCount();
            incrementUnreadChatCounter(chatOfMessage);
            // вот это может быть опасно с точки зрения синхронизации (которой у нас нет, ахах)
            changeSupport.firePropertyChange("chatReceivedMessages", null, chatOfMessage);
        }

        Set<Long> newChatIds = (msgInExistingChat.get(false).stream()
                .map(Message::getChatId)
                .collect(Collectors.toSet()));
        newChatIds.forEach(this::fetchChatFromServer);
    }

    private void fetchChatFromServer(Long chatId) {
        handler.sendGETAsync("chats/" + chatId)
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                .thenAccept(chat -> {
                    changeSupport.firePropertyChange("NewChatCreated", null, chat);
                    myChats.add(0, chat);
                });
    }


    private CompletableFuture<Chat> futureChatWith(User user) {
        return handler.sendGETAsync(String.format("/private_chat/%d/%d", myself.getId(), user.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class));
    }

    @Deprecated
    private CompletableFuture<Chat> futureGroupWith(ArrayList<User> users) {
        return handler.sendPOSTAsync(
                        "/chat?isPrivate=false",
                        Stream.concat(users.stream().map(User::getId).toList().stream(), Stream.of(myself.getId())).toList(),
                        true
                )
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class));
    }

    @Override
    @Deprecated
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
    @Deprecated
    public Chat createDialogFromAllUsers(User user) {
        try {
            Chat chat = futureChatWith(user).get();
            if (!myChats.contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
                myChats.add(0, chat);
            }
            return chat;
        } catch (Exception e) {
            System.out.println("Fail to Create dialog");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Deprecated
    public void createDialogFromNewMessage(User user) {
        futureChatWith(user).thenAccept(chat -> {
            chat.incrementUnreadCount();
            myChats.add(chat);
            changeSupport.firePropertyChange("NewChatCreated", null, chat);
            incrementUnreadChatCounter(chat);
        });
    }

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }
}
