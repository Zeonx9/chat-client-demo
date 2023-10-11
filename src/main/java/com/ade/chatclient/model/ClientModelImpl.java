package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.*;
import com.ade.chatclient.dtos.*;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
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
    private boolean isAdmin;
    private List<Chat> myChats = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public ClientModelImpl(AsyncRequestHandler handler) {
//        System.out.println("model created!");
        this.handler = handler;
    }

    @Override
    public boolean authorize(String login, String password) {
//        System.out.println("Authorize request: " + login);

        if (myself != null) {
//            System.out.println("Попытка ре-авторизации");
            return true;
        }

        return authorizeRequest(login, password);
    }

    @Override
    public void clearModel() {
        setMyself(null);
        setSelectedChat(null);
        setCompany(null);
        getMyChats().clear();
        getAllUsers().clear();
    }

    private boolean authorizeRequest(String login, String password) {
        try {
            AuthResponse auth = handler.sendPost(
                    "/auth/login",
                    AuthRequest.builder().login(login).password(password).build(),
                    AuthResponse.class, false
            ).get();

            setMyself(auth.getUser());
            setCompany(auth.getCompany());
            setAdmin(auth.isAdmin());
            handler.setAuthToken(auth.getToken());
        }
        catch (Exception e) {
//            System.out.println("login failed\n" + e.getMessage());
            return false;
        }
//        System.out.println("login OK");
        return true;
    }

    @Override
    public AuthRequest registerUser(RegisterData data) {
        try {
            return handler.sendPost("/auth/register", data, AuthRequest.class, true).get();
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @param chats список чатов который присваивается списку чатов пользователя
     */
    public void setMyChats(List<Chat> chats){
        synchronized (this) {
            myChats = chats;
        }
    }

    /**
     * @return возвращает значение списка чатов пользователя
     */
    public List<Chat> getMyChats(){
        synchronized (this) {
            return myChats;
        }
    }

    @Override
    public synchronized void fetchChats() {
//        System.out.println("chats fetched");
        if (myself == null) {
            return;
        }

        try {
            List<Chat> chats = handler.sendGet(String.format("/users/%d/chats", myself.getId()), TypeReferences.ListOfChat).get();
            changeSupport.firePropertyChange("gotChats", null, chats);
            setMyChats(chats);
        }
        catch (Exception e){
//            System.out.println("error");
        }

    }

    private void fetchChatMessages() {
        if (getSelectedChat() == null) {
            return;
        }

        Chat copySelectedChat = getSelectedChat();

        handler.sendGet(
                    String.format("/chats/%d/messages", copySelectedChat.getId()),
                    Map.of("userId", myself.getId().toString()),
                    TypeReferences.ListOfMessage
                )
                .thenAccept(messages -> {
                    synchronized (this) {
                        if (!getSelectedChat().equals(copySelectedChat)) return;
                        getSelectedChat().setUnreadCount(0);
                        changeSupport.firePropertyChange("gotMessages", null, messages);
                        changeSupport.firePropertyChange("selectedChatModified", null, getSelectedChat());
                    }
                });
    }

    @Override
    public void setSelectChat(Chat chat) {
//        System.out.println("chat selected!");
        synchronized (this) {
            selectedChat = chat;
        }
        fetchChatMessages();
    }

    @Override
    public Chat getSelectedChat() {
        synchronized (this) {
            return selectedChat;
        }
    }

    private void sortMyChats(Chat chat) {
        getMyChats().remove(chat);
        getMyChats().add(0, chat);
    }

    @Override
    public void sendMessageToChat(String text) {
        if (selectedChat == null) {
            return;
        }
//        System.out.println("sending message...");

        Chat copySelectedChat;
        synchronized (this) {
            copySelectedChat = getSelectedChat();
            Message mes = Message.builder().text(text).author(myself).dateTime(LocalDateTime.now()).chatId(getSelectedChat().getId()).build();
            changeSupport.firePropertyChange("newMessagesInSelected", null, List.of(mes));
            selectedChat.setLastMessage(mes);
            changeSupport.firePropertyChange("chatReceivedMessages", true, copySelectedChat);
            sortMyChats(copySelectedChat);
        }
        handler.sendPost(
                        String.format("/users/%d/chats/%d/message", myself.getId(), copySelectedChat.getId()),
                        Message.builder().text(text).dateTime(LocalDateTime.now()).build(),
                        Message.class, true
                );

    }

    @Override
    public void fetchUsers() {
//        System.out.println("fetching users");
        handler.sendGet("/company/" + company.getId() + "/users", TypeReferences.ListOfUser)
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
        handler.sendGet(String.format("/users/%d/undelivered_messages", myself.getId()), TypeReferences.ListOfMessage)
                .thenAccept(this::acceptNewMessages);
    }

    private void acceptNewMessages(List<Message> newMessages) {
        if (newMessages.isEmpty()) {
            return;
        }
//        System.out.println("new messages!");

        synchronized (this) {
            Map<Boolean, List<Message>> msgInSelectedChat = newMessages.stream().collect(Collectors.partitioningBy(
                    message -> getSelectedChat() != null && message.getChatId().equals(getSelectedChat().getId())
            ));
            if (!msgInSelectedChat.get(true).isEmpty()) {
                getSelectedChat().setLastMessage(msgInSelectedChat.get(true).get(0));
                changeSupport.firePropertyChange("newMessagesInSelected", null, msgInSelectedChat.get(true));
                changeSupport.firePropertyChange("chatReceivedMessages", true, getSelectedChat());
                sortMyChats(getSelectedChat());
            }
            if (!msgInSelectedChat.get(false).isEmpty()) {
                processMessagesInOtherChats(msgInSelectedChat.get(false));
            }
        }
    }

    private void processMessagesInOtherChats(List<Message> messages) {
        Map<Long, Chat> chatById = getMyChats().stream()
                .collect(Collectors.toMap(Chat::getId, Function.identity()));
        Map <Boolean, List<Message>> msgInExistingChat = messages.stream()
                .collect(Collectors.partitioningBy(mes -> chatById.containsKey(mes.getChatId())));

        for (Message msg : msgInExistingChat.get(true)) {
            Chat chatOfMessage = chatById.get(msg.getChatId());
            chatOfMessage.incrementUnreadCount();
            chatOfMessage.setLastMessage(msg);

            changeSupport.firePropertyChange("chatReceivedMessages", false, chatOfMessage);
            sortMyChats(chatOfMessage);
        }

        Set<Long> newChatIds = (msgInExistingChat.get(false).stream()
                .map(Message::getChatId)
                .collect(Collectors.toSet()));
        if (newChatIds.size() > 0) {
//            System.out.println("New chats recieved! (" + newChatIds.size() + ")");
        }
        newChatIds.forEach(this::fetchNewChatFromServer);
    }

    private void fetchNewChatFromServer(Long chatId) {
//        System.out.println("fetching one chat");
        handler.sendGet("/chats/" + chatId, Chat.class)
                .thenAccept(chat -> {
//                    System.out.println("creation of new chat:" + chat.getId());
                    changeSupport.firePropertyChange("NewChatCreated", null, chat);
                    synchronized (this) {
                        myChats.add(0, chat);
                    }
                });
    }


    private CompletableFuture<Chat> futureChatWith(User user) {
        return handler.sendGet(String.format("/private_chat/%d/%d", myself.getId(), user.getId()), Chat.class);
    }


    private CompletableFuture<Chat> futureGroupWith(GroupRequest groupRequest) {
        return handler.sendPost("/group_chat", groupRequest, Chat.class, true);
    }

    @Override
    public void createGroupChat(GroupRequest groupRequest) {
//        System.out.println("creating group");
        try {
            groupRequest.getIds().add(myself.getId());
            groupRequest.getGroupInfo().setCreator(myself);

            Chat chat = futureGroupWith(groupRequest).get();
            if (!getMyChats().contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
                synchronized (this) {
                    myChats.add(0, chat);
                }
            }
        } catch (Exception e) {
//            System.out.println("Fail to Create group");
        }
    }

    @Override
    public void getMyChatsAfterSearching() {
        changeSupport.firePropertyChange("gotChats", null, getMyChats());
    }

    @Override
    public void getAllUsersAfterSearching() {
        changeSupport.firePropertyChange("AllUsers", null, getAllUsers());
    }

    @Override
    public Chat createDialogFromAllUsers(User user) {
        try {
//            System.out.println("opening chat wiht " + user.getId());
            Chat chat = futureChatWith(user).get();
            if (!getMyChats().contains(chat)) {
//                System.out.println("it was a new chat");
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
                synchronized (this) {
                    myChats.add(0, chat);
                }
            }
            return chat;
        } catch (Exception e) {
//            System.out.println("Fail to Create dialog");
//            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getChatNameForSearch(Chat chat) {
        if (chat.getGroup() != null) {
            return chat.getGroup().getName();
        }
        var result = chat.getMembers().stream().map(User::getUsername)
                .filter(username -> !username.equals(myself.getUsername())).toList();
        return String.join("", result);
    }

    private boolean isRequested(Chat chat, String request) {
        return chat.getIsPrivate() && !chat.getMembers().stream()
                .filter(user -> isRequested(user, request) && !user.equals(myself)).toList().isEmpty();
    }

    @Override
    public List<Chat> searchChat(String request) {
        return getMyChats().stream()
                .filter(chat -> getChatNameForSearch(chat).toLowerCase().startsWith(request.toLowerCase())
                        || isRequested(chat, request.toLowerCase()))
                .toList();
    }

    private boolean isRequested(User user, String request) {
        return user.getUsername().toLowerCase().startsWith(request)
                || (user.getSurname().toLowerCase().startsWith(request))
                || (user.getRealName().toLowerCase().startsWith(request))
                || ((user.getRealName().toLowerCase() + " " + user.getSurname().toLowerCase()).startsWith(request))
                || ((user.getSurname().toLowerCase() + " " + user.getRealName().toLowerCase()).startsWith(request));
    }

    @Override
    public List<User> searchUser(String request) {
        return allUsers.stream().filter(user -> isRequested(user, request.toLowerCase())).toList();
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        request.getAuthRequest().setLogin(myself.getUsername());
        handler.sendPut("/auth/user/password", request, AuthResponse.class)
                .thenAccept(response -> {
                        changeSupport.firePropertyChange("passwordChangeResponded", null, "successfully!");
                        changeSupport.firePropertyChange("savePassword", null, request.getNewPassword());
                }
                ).exceptionally(e -> {
                    changeSupport.firePropertyChange("passwordChangeResponded", null, "unsuccessful attempt!");
//                    System.out.println(e.getMessage());
                    return null;
                });
    }

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }

}
