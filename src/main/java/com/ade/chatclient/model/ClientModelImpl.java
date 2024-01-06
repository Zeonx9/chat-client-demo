package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.application.Settings;
import com.ade.chatclient.application.SettingsManager;
import com.ade.chatclient.api.AuthorizationApi;
import com.ade.chatclient.api.StompSessionApi;
import com.ade.chatclient.repository.ChatRepository;
import com.ade.chatclient.repository.MessageRepository;
import com.ade.chatclient.domain.*;
import com.ade.chatclient.dtos.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class ClientModelImpl implements ClientModel{
    private final AsyncRequestHandler handler;
    private final AuthorizationApi authApi;
    private final StompSessionApi stompSessionApi;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    private User myself;
    private Chat selectedChat;
    private Company company;
    private boolean isAdmin;
    private List<User> allUsers = new ArrayList<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);


    @Override
    public boolean authorize(String login, String password) {
        if (myself != null) {
            return true;
        }
        return authorizeRequest(login, password);
    }

    @Override
    public void clearModel() {
        setMyself(null);
        setSelectedChat(null);
        setCompany(null);
        chatRepository.clearChats();
        getAllUsers().clear();
    }

    private boolean authorizeRequest(String login, String password) {
        try {
            AuthResponse auth = authApi.authorize(login, password);

            setMyself(auth.getUser());
            setCompany(auth.getCompany());
            setAdmin(auth.isAdmin());
            handler.setAuthToken(auth.getToken());
        }
        catch (Exception e) {
            log.error("login failed", e);
            return false;
        }
        log.info("authorized successfully");
        messageRepository.setSelfId(myself.getId());
        chatRepository.setSelfId(myself.getId());
        return true;
    }

    @Override
    public AuthRequest registerUser(RegisterData data) {
        try {
            return authApi.registerUser(data);
        } catch (Exception e) {
            log.error("user registration failed", e);
            return null;
        }
    }


    @Override
    public synchronized void fetchChats() {
        if (myself == null) {
            return;
        }
        chatRepository.fetchChats().thenAccept(chats ->
                changeSupport.firePropertyChange("gotChats", null, chats)
        );
    }

    private void fetchChatMessages() {
        if (getSelectedChat() == null) {
            return;
        }

        Chat copySelectedChat = getSelectedChat();
        messageRepository.getMessagesOfChat(copySelectedChat.getId()).thenAccept(messages -> {
            synchronized (this) {
                if (!getSelectedChat().equals(copySelectedChat)) {
                    return;
                }
                getSelectedChat().setUnreadCount(0);
                changeSupport.firePropertyChange("gotMessages", null, messages);
                changeSupport.firePropertyChange("selectedChatModified", null, getSelectedChat());
            }
        });
    }

    @Override
    public void setSelectChat(Chat chat) {
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

    @Override
    public void sendMessageToChat(String text) {
        if (selectedChat == null) {
            return;
        }
        messageRepository.sendMessageToChat(text, getSelectedChat().getId());
    }

    @Override
    public void fetchUsers() {
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

    }

    @Override
    public void createGroupChat(GroupRequest groupRequest) {
        groupRequest.getIds().add(myself.getId());
        groupRequest.getGroupInfo().setCreator(myself);
        chatRepository.createNewGroupChat(groupRequest);
    }

    @Override
    public void getMyChatsAfterSearching() {
        changeSupport.firePropertyChange("gotChats", null, chatRepository.getChats());
    }

    @Override
    public void getAllUsersAfterSearching() {
        changeSupport.firePropertyChange("AllUsers", null, getAllUsers());
    }

    @Override
    public Chat createDialogFromAllUsers(User user) {
        try {
            Chat chat = chatRepository.createNewPrivateChat(user.getId()).get();
            if (!chatRepository.getChats().contains(chat)) {
                changeSupport.firePropertyChange("NewChatCreated", null, chat);
            }
            return chat;
        } catch (Exception e) {
            log.error("failed to create dialog from all users ", e);
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
        return chatRepository.getChats().stream()
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
                        SettingsManager.changeSettings(Settings::setPassword, request.getNewPassword());
                }
                ).exceptionally(e -> {
                    changeSupport.firePropertyChange("passwordChangeResponded", null, "unsuccessful attempt!");
                    log.error("failed to change password", e);
                    return null;
                });
    }

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(eventName, listener);
    }

    @Override
    public void startWebSocketConnection() {
        stompSessionApi.connect();
        stompSessionApi.subscribeTopicConnection();
        stompSessionApi.subscribeQueueMessages(myself.getId(), this::acceptNewMessage);
        stompSessionApi.subscribeQueueChats(myself.getId(), this::acceptNewChat);
        stompSessionApi.sendConnectSignal(myself);
    }

    @Override
    public void stopWebSocketConnection() {
        if (stompSessionApi != null) {
            stompSessionApi.sendDisconnectSignal(myself);
        }
    }

    private void acceptNewChat(Chat chat) {
        changeSupport.firePropertyChange("NewChatCreated", null, chat);
        chatRepository.acceptNewChat(chat);
    }

    private void acceptNewMessage(Message message) {
        if (Objects.equals(message.getChatId(), getSelectedChat().getId())) {
            getSelectedChat().setLastMessage(message);
            changeSupport.firePropertyChange("newMessagesInSelected", null, List.of(message));
            changeSupport.firePropertyChange("chatReceivedMessages", true, getSelectedChat());
            chatRepository.moveChatUp(getSelectedChat());
        } else {
            Chat chatOfMessage = chatRepository.getChatById(message.getChatId());
            chatOfMessage.incrementUnreadCount();
            chatOfMessage.setLastMessage(message);
            changeSupport.firePropertyChange("chatReceivedMessages", false, chatOfMessage);
            chatRepository.moveChatUp(chatOfMessage);
        }
        messageRepository.acceptNewMessage(message);
    }

}
