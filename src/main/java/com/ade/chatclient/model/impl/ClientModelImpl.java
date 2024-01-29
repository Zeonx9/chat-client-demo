package com.ade.chatclient.model.impl;

import com.ade.chatclient.api.StompSessionApi;
import com.ade.chatclient.application.Settings;
import com.ade.chatclient.application.SettingsManager;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.dtos.ConnectEvent;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.dtos.ReadNotification;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.repository.ChatRepository;
import com.ade.chatclient.repository.MessageRepository;
import com.ade.chatclient.repository.SelfRepository;
import com.ade.chatclient.repository.UsersRepository;
import com.ade.chatclient.viewmodel.AllChatsViewModel;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import com.ade.chatclient.viewmodel.UserSettingsViewModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class ClientModelImpl implements ClientModel {
    private final StompSessionApi stompSessionApi;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UsersRepository usersRepository;
    private final SelfRepository selfRepository;

    private User myself;
    private Chat selectedChat;
    private Company company;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    @Override
    public void runModel() {
        myself = selfRepository.getMyself();
        company = selfRepository.getCompany();
        setIdToRepo();
        startWebSocketConnection();
        fetchUsers();
        fetchChats();
    }

    private void setIdToRepo() {
        chatRepository.setSelfId(myself.getId());
        messageRepository.setSelfId(myself.getId());
        usersRepository.setSelfId(myself.getId());
    }

    @Override
    public void clearModel() {
        myself = null;
        setSelectedChat(null);
        setCompany(null);
        chatRepository.clearChats();
        usersRepository.clearUsers();
        selfRepository.clear();
    }

    @Override
    public synchronized void fetchChats() {
        if (myself == null) {
            return;
        }
        chatRepository.fetchChats().thenAccept(chats ->
                changeSupport.firePropertyChange(AllChatsViewModel.GOT_CHATS_EVENT, null, chats)
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
                changeSupport.firePropertyChange(ChatPageViewModel.GOT_MESSAGES_EVENT, null, messages);
                changeSupport.firePropertyChange(AllChatsViewModel.SELECTED_CHAT_MODIFIED_EVENT, null, getSelectedChat());
            }
        });
    }

    @Override
    public void setSelectChat(Chat chat) {
        synchronized (this) {
            selectedChat = chat;
        }
        fetchChatMessages();
        stompSessionApi.sendReadChatSignal(new ReadNotification(myself.getId(), chat.getId()));
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
        usersRepository.fetchUsers(company.getId())
                .thenAccept(userList ->
                        changeSupport.firePropertyChange(AllUsersViewModel.ALL_USERS_EVENT, null, userList));
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepository.getUsers();
    }

    @Override
    public void createGroupChat(GroupRequest groupRequest) {
        groupRequest.getIds().add(myself.getId());
        groupRequest.getGroupInfo().setCreator(myself);
        chatRepository.createNewGroupChat(groupRequest);
    }

    @Override
    public void getMyChatsAfterSearching() {
        changeSupport.firePropertyChange(AllChatsViewModel.GOT_CHATS_EVENT, null, chatRepository.getChats());
    }

    @Override
    public void getAllUsersAfterSearching() {
        changeSupport.firePropertyChange(AllUsersViewModel.ALL_USERS_EVENT, null, usersRepository.getUsers());
    }

    @Override
    public Chat createDialogFromAllUsers(User user) {
        try {
            Chat chat = chatRepository.createNewPrivateChat(user.getId()).get();
            if (!chatRepository.getChats().contains(chat)) {
                changeSupport.firePropertyChange(AllChatsViewModel.NEW_CHAT_CREATED_EVENT, null, chat);
            }
            return chat;
        } catch (Exception e) {
            log.error("failed to create dialog from all users ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Chat> searchChat(String request) {
        return chatRepository.search(request);
    }

    @Override
    public List<User> searchUser(String request) {
        return usersRepository.search(request);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        request.getAuthRequest().setLogin(myself.getUsername());
        selfRepository.changePassword(request)
                .thenAccept(response -> {
                            changeSupport.firePropertyChange(UserSettingsViewModel.CHANGED_RESPONDED_EVENT, null, "successfully!");
                            SettingsManager.changeSettings(Settings::setPassword, request.getNewPassword());
                        }
                ).exceptionally(e -> {
                    changeSupport.firePropertyChange(UserSettingsViewModel.CHANGED_RESPONDED_EVENT, null, "unsuccessful attempt!");
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
        stompSessionApi.subscribeTopicConnection(company.getId(), this::acceptNewConnectEvent);
        stompSessionApi.subscribeQueueMessages(myself.getId(), this::acceptNewMessage);
        stompSessionApi.subscribeQueueChats(myself.getId(), this::acceptNewChat);
        stompSessionApi.subscribeQueueReadNotifications(myself.getId(), this::acceptNewReadNotification);
        stompSessionApi.sendConnectSignal(myself);
    }

    @Override
    public void stopWebSocketConnection() {
        if (stompSessionApi != null) {
            stompSessionApi.sendDisconnectSignal(myself);
        }
    }

    @Override
    public void changeUserInfo(User newUserInfo) {
        newUserInfo.setId(myself.getId());
        newUserInfo.setUsername(myself.getUsername());
        newUserInfo.setIsOnline(true);
        selfRepository.changeUserInfo(newUserInfo)
                .thenAccept(response -> {
                            changeSupport.firePropertyChange(UserSettingsViewModel.CHANGED_RESPONDED_EVENT, null, "successfully!");
                            myself = response;
                            setIdToRepo();
                        }
                ).exceptionally(e -> {
                    changeSupport.firePropertyChange(UserSettingsViewModel.CHANGED_RESPONDED_EVENT, null, "unsuccessful attempt!");
                    log.error("failed to change user info", e);
                    return null;
                });
    }

    private void acceptNewChat(Chat chat) {
        changeSupport.firePropertyChange(AllChatsViewModel.NEW_CHAT_CREATED_EVENT, null, chat);
        chatRepository.acceptNewChat(chat);
    }

    private void acceptNewMessage(Message message) {
        if (Objects.equals(message.getChatId(), getSelectedChat().getId())) {
            getSelectedChat().setLastMessage(message);
            changeSupport.firePropertyChange(ChatPageViewModel.NEW_MESSAGES_IN_SELECTED_EVENT, null, List.of(message));
            changeSupport.firePropertyChange(AllChatsViewModel.CHAT_RECEIVED_MESSAGES_EVENT, true, getSelectedChat());
            chatRepository.moveChatUp(getSelectedChat());
        } else {
            Chat chatOfMessage = chatRepository.getChatById(message.getChatId());
            chatOfMessage.incrementUnreadCount();
            chatOfMessage.setLastMessage(message);
            changeSupport.firePropertyChange(AllChatsViewModel.CHAT_RECEIVED_MESSAGES_EVENT, false, chatOfMessage);
            chatRepository.moveChatUp(chatOfMessage);
        }
        messageRepository.acceptNewMessage(message);
    }

    private void acceptNewReadNotification(ReadNotification notification) {

    }

    private void acceptNewConnectEvent(ConnectEvent event) {

    }

}
