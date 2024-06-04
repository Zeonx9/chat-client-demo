package com.ade.chatclient.model.impl;

import com.ade.chatclient.api.StompSessionApi;
import com.ade.chatclient.application.Settings;
import com.ade.chatclient.application.SettingsManager;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.*;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.repository.*;
import com.ade.chatclient.viewmodel.*;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    private final FileRepository fileRepository;

    private Chat selectedChat;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    @Override
    public void runModel() {
        setIdToRepo();
        startWebSocketConnection();
        fetchUsers();
        fetchChats();
    }

    private void setIdToRepo() {
        chatRepository.setSelfId(selfRepository.getMyself().getId());
        messageRepository.setSelfId(selfRepository.getMyself().getId());
        usersRepository.setSelfId(selfRepository.getMyself().getId());
    }

    @Override
    public void clearModel() {
        stopWebSocketConnection();
        setSelectedChat(null);
        chatRepository.clearChats();
        usersRepository.clearUsers();
        messageRepository.clear();
        selfRepository.clear();
        fileRepository.clear();
    }

    @Override
    public synchronized void fetchChats() {
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
        stompSessionApi.sendReadChatSignal(new ReadNotification(selfRepository.getMyself().getId(), chat.getId()));
    }

    @Override
    public Chat getSelectedChat() {
        synchronized (this) {
            return selectedChat;
        }
    }

    @Override
    public void sendMessageToChat(String text, File photo) {
        if (selectedChat == null) {
            return;
        }
        if (photo == null) {
            messageRepository.sendMessageToChat(text, getSelectedChat().getId(), null);
        } else {
            uploadPhoto(photo).thenAccept(photoId ->
                    messageRepository.sendMessageToChat(text, getSelectedChat().getId(), photoId));
        }
    }

    private CompletableFuture<String> uploadPhoto(File photo) {
        return fileRepository.uploadPhoto(Path.of(photo.toString()));
    }

    @Override
    public void fetchUsers() {
        usersRepository.fetchUsers(selfRepository.getCompany().getId())
                .thenAccept(userList ->
                        changeSupport.firePropertyChange(AllUsersViewModel.ALL_USERS_EVENT, null, userList));
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepository.getUsers();
    }

    @Override
    public void createGroupChat(GroupRequest groupRequest) {
        groupRequest.getIds().add(selfRepository.getMyself().getId());
        groupRequest.getGroupInfo().setCreator(selfRepository.getMyself());
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
    public Company getCompany() {
        return selfRepository.getCompany();
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        request.getAuthRequest().setLogin(selfRepository.getMyself().getUsername());
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
    public User getMyself() {
        return selfRepository.getMyself();
    }

    @Override
    public void startWebSocketConnection() {
        stompSessionApi.connect();
        stompSessionApi.subscribeTopicConnection(selfRepository.getCompany().getId(), this::acceptNewConnectEvent);
        stompSessionApi.subscribeQueueMessages(selfRepository.getMyself().getId(), this::acceptNewMessage);
        stompSessionApi.subscribeQueueChats(selfRepository.getMyself().getId(), this::acceptNewChat);
        stompSessionApi.subscribeQueueReadNotifications(selfRepository.getMyself().getId(), this::acceptNewReadNotification);
        stompSessionApi.sendConnectSignal(selfRepository.getMyself());
    }

    @Override
    public void stopWebSocketConnection() {
        if (stompSessionApi != null) {
            stompSessionApi.sendDisconnectSignal(selfRepository.getMyself());
        }
    }

    @Override
    public void changeUserInfo(User newUserInfo) {
        newUserInfo.setId(selfRepository.getMyself().getId());
        newUserInfo.setUsername(selfRepository.getMyself().getUsername());
        newUserInfo.setIsOnline(true);
        selfRepository.changeUserInfo(newUserInfo)
                .thenAccept(response -> {
                            changeSupport.firePropertyChange(UserSettingsViewModel.CHANGED_RESPONDED_EVENT, null, "successfully!");
                            changeSupport.firePropertyChange(ProfileViewModel.CHANGED_USER_INFO, null, response);
                            selfRepository.setMyself(response);
                            setIdToRepo();
                        }
                ).exceptionally(e -> {
                    changeSupport.firePropertyChange(UserSettingsViewModel.CHANGED_RESPONDED_EVENT, null, "unsuccessful attempt!");
                    log.error("failed to change user info", e);
                    return null;
                });
    }

    @Override
    public void uploadUserProfilePhoto(File photo) {
        selfRepository.changeProfilePhoto(Path.of(photo.toString()))
                .thenAccept(newPhoto ->
                        changeSupport.firePropertyChange(ProfileViewModel.CHANGED_USER_INFO, null, getMyself()));
    }

    @Override
    public void uploadGroupChatPhoto(File photo) {
        chatRepository.editGroupChatPhoto(selectedChat.getId(), Path.of(photo.toString()))
                .thenAccept(chat -> {
                            changeSupport.firePropertyChange(AllChatsViewModel.UPDATE_CHAT_INFO, null, chat);
                            changeSupport.firePropertyChange(ChatPageViewModel.GROUP_PHOTO, null, chat);
                            changeSupport.firePropertyChange(GroupInfoDialogModel.NEW_GROUP_CHAT_INFO, null, chat);
                        }
                );
    }

    @Override
    public CompletableFuture<Image> getPhotoById(String photoId) {
        return fileRepository.getFile(photoId).thenApply(bytes -> new Image(new ByteArrayInputStream(bytes)));
    }


    private void acceptNewChat(Chat chat) {
        changeSupport.firePropertyChange(AllChatsViewModel.NEW_CHAT_CREATED_EVENT, null, chat);
        chatRepository.acceptNewChat(chat);
    }

    private void acceptNewMessage(Message message) {
        if (getSelectedChat() != null && Objects.equals(message.getChatId(), getSelectedChat().getId())) {
            getSelectedChat().setLastMessage(message);
            changeSupport.firePropertyChange(ChatPageViewModel.NEW_MESSAGES_IN_SELECTED_EVENT, null, List.of(message));
            changeSupport.firePropertyChange(AllChatsViewModel.CHAT_RECEIVED_MESSAGES_EVENT, true, getSelectedChat());
            chatRepository.moveChatUp(getSelectedChat());
        } else {
            Chat chatOfMessage = chatRepository.getChatById(message.getChatId());
            if (message.getAuthor() != null) chatOfMessage.incrementUnreadCount();
            chatOfMessage.setLastMessage(message);
            changeSupport.firePropertyChange(AllChatsViewModel.CHAT_RECEIVED_MESSAGES_EVENT, false, chatOfMessage);
            chatRepository.moveChatUp(chatOfMessage);
        }
        messageRepository.acceptNewMessage(message);
    }

    private void acceptNewReadNotification(ReadNotification notification) {

    }

    private void acceptNewConnectEvent(ConnectEvent event) {
        if (Objects.equals(event.getUserId(), getMyself().getId())) {
            return;
        }
        changeSupport.firePropertyChange(AllUsersViewModel.UPDATE_USER_ONLINE,
                null,
                usersRepository.updateOnlineStatus(event.getUserId(), event.getConnect()));

        chatRepository.updateOnlineForChatMembers(event.getUserId(), event.getConnect());
        changeSupport.firePropertyChange(AllChatsViewModel.UPDATE_MEMBERS_ONLINE, null, event);

    }

    @Override
    public void editGroupName(String chatName) {
        chatRepository.editGroupName(selectedChat.getId(), ChangeGroupName.builder().groupName(chatName).build())
                .thenAccept(chat -> {
                    changeSupport.firePropertyChange(AllChatsViewModel.UPDATE_CHAT_INFO, null, chat);
                    changeSupport.firePropertyChange(
                                    ChatPageViewModel.GROUP_CHAT_NAME_UPDATE,
                                    null,
                                    chat.getGroup().getName());
                        }
                );
    }

    @Override
    public void addUserToGroupChat(long userId) {
        chatRepository.addUser(selectedChat.getId(), userId).thenApply(chat -> {
            changeSupport.firePropertyChange(GroupInfoDialogModel.NEW_GROUP_CHAT_INFO, null, chat);
            return chat;
        });
    }

    @Override
    public void removeUserFromGroupChat(long userId) {
        chatRepository.removeUser(selectedChat.getId(), userId).thenApply(chat -> {
            changeSupport.firePropertyChange(GroupInfoDialogModel.NEW_GROUP_CHAT_INFO, null, chat);
            return chat;
        });
    }

}
