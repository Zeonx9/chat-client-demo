package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.*;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.GroupRequest;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;


@Getter
@Setter
public class ClientModelImpl implements ClientModel{
    private final AsyncRequestHandler handler;
    private User myself;
    private Chat selectedChat;

    private Company company;
    private List<Chat> myChats = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private long unreadChatCounter;

    public ClientModelImpl(AsyncRequestHandler handler) {
        System.out.println("model created!");
        this.handler = handler;
    }

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
            setCompany(auth.getCompany());
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
        System.out.println("chats fetched");
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
        System.out.println("fetch messages " + selectedChat.getId());
        decrementChatCounter(selectedChat);
        handler.sendGETAsync(
                    String.format("/chats/%d/messages", selectedChat.getId()),
                    Map.of("userId", myself.getId().toString())
                )
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(messages -> {
                    selectedChat.setUnreadCount(0);
                    changeSupport.firePropertyChange("gotMessages", null, messages);
                    changeSupport.firePropertyChange("selectedChatModified", null, selectedChat);
                });
    }

    @Override
    public void selectChat(Chat chat) {
        System.out.println("chat selected!");
        selectedChat = chat;
        fetchChatMessages();
    }

    @Override
    public void sendMessageToChat(String text) {
        if (selectedChat == null) {
            return;
        }
        System.out.println("sending message...");
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
        System.out.println("fetching users");
        handler.sendGETAsync("/company/" + company.getId() + "/users")
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
        if (newMessages.isEmpty()) {
            return;
        }
        System.out.println("new messages!");
        Map<Boolean, List<Message>> msgInSelectedChat = newMessages.stream().collect(Collectors.partitioningBy(
                message -> selectedChat != null && message.getChatId().equals(selectedChat.getId())
        ));
        if (!msgInSelectedChat.get(true).isEmpty()){
            selectedChat.setLastMessage(msgInSelectedChat.get(true).get(0));
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
            chatOfMessage.setLastMessage(msg);
            // вот это может быть опасно с точки зрения синхронизации (которой у нас нет, ахах)
            changeSupport.firePropertyChange("chatReceivedMessages", null, chatOfMessage);
        }

        Set<Long> newChatIds = (msgInExistingChat.get(false).stream()
                .map(Message::getChatId)
                .collect(Collectors.toSet()));
        if (newChatIds.size() > 0) {
            System.out.println("New chats recieved! (" + newChatIds.size() + ")");
        }
        newChatIds.forEach(this::fetchNewChatFromServer);
    }

    private void fetchNewChatFromServer(Long chatId) {
        System.out.println("fetching one chat");
        handler.sendGETAsync("/chats/" + chatId)
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                .thenAccept(chat -> {
                    System.out.println("creation of new chat:" + chat.getId());
                    changeSupport.firePropertyChange("NewChatCreated", null, chat);
                    myChats.add(0, chat);
                });
    }


    private CompletableFuture<Chat> futureChatWith(User user) {
        return handler.sendGETAsync(String.format("/private_chat/%d/%d", myself.getId(), user.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class));
    }


    private CompletableFuture<Chat> futureGroupWith(GroupRequest groupRequest) {
        return handler.sendPOSTAsync("/group_chat", groupRequest, true)
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class));
    }

    @Override
    public void createGroupChat(GroupRequest groupRequest) {
        System.out.println("creating group");
        try {
            groupRequest.getIds().add(myself.getId());
            groupRequest.getGroupInfo().setCreator(myself);

            Chat chat = futureGroupWith(groupRequest).get();
            if (!myChats.contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
                myChats.add(0, chat);
            }
        } catch (Exception e) {
            System.out.println("Fail to Create dialog");
            throw new RuntimeException(e);
        }
    }


    @Override
    public Chat createDialogFromAllUsers(User user) {
        try {
            System.out.println("opening chat wiht " + user.getId());
            Chat chat = futureChatWith(user).get();
            if (!myChats.contains(chat)) {
                System.out.println("it was a new chat");
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
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }
}
