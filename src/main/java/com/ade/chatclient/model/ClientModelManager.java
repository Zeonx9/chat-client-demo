package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.model.entities.Chat;
import com.ade.chatclient.model.entities.Message;
import com.ade.chatclient.model.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// realization of Client model interface manages and manipulates the data
public class ClientModelManager implements ClientModel{
    private final RequestHandler handler;
    private User self;
    private List<Chat> myChats;
    private List<Message> selectedChatMessages;
    private Chat selectedChat;
    private List<User> users;

    public ClientModelManager(RequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public User getMyself() {
        return self;
    }

    @Override
    public List<Chat> getMyChats() {
        if (self == null)
            throw new RuntimeException("attempt to get chats before log in");
        if (myChats == null) {
            updateMyChats();
        }
        return myChats;
    }

    @Override
    public void updateMyChats() {
        myChats = handler.mapResponse(
                handler.GETRequest(String.format("/users/%d/chats", self.getId())),
                new TypeReference<>(){}
        );
    }

    @Override
    public boolean Authorize(String login) {
        System.out.println("Authorize request: " + login);
        if (self == null) {
            try {
                self = handler.mapResponse(
                        handler.GETRequest("/user", Map.of("name", login)),
                        User.class
                );
            }
            catch (Exception e) {
                System.out.println("Authorization failed");
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void selectChat(Chat chat) {
        if (self == null || myChats == null)
            throw new RuntimeException("attempt to get chats before log in or you don't have chats");
        selectedChat = chat;

        boolean hasSuchChat = false;
        for (Chat myChat : myChats) {
            if (myChat.getId().equals(chat.getId())) {
                hasSuchChat = true;
                break;
            }
        }
        if (hasSuchChat)
            updateMessages();
        else
            System.out.println("User does not has a chat with id: " + chat.getId());
    }


    @Override
    public void updateMessages() {
        selectedChatMessages = handler.mapResponse(
                handler.GETRequest(String.format("/chats/%d/messages", selectedChat.getId())),
                new TypeReference<>() {
                });
    }

    @Override
    public List<Message> getSelectedChatMessages() {
        return selectedChatMessages;
    }

    @Override
    public void sendMessageToChat(String text) {
        handler.sendPOST(
                handler.POSTRequest(String.format("/users/%d/chats/%d/message", self.getId(), selectedChat.getId()),
                prepareMessage(text)));
    }

    private String prepareMessage(String text) {
        return String.format("{ \"text\": \"%s\" }", text);
    }

    @Override
    public List<User> getAllUsers() {
        if (users == null) {
            allUsers();
        }
        return users;
    }

    private void allUsers() {
        if (self == null)
            throw new RuntimeException("attempt to get chats before log in");
        users = handler.mapResponse(
                handler.GETRequest("/users"),
                new TypeReference<>() {
                });
    }

    @Override
    public void sendMessageToUser(String text, User user) {
        handler.sendPOST(handler.POSTRequest(String.format("/users/%d/message/users/%d", self.getId(), user.getId()),
                prepareMessage(text)));
    }

    @Override
    public void createDialog(User user) {
        handler.sendPOST(handler.POSTRequest("/chat?isPrivate=true",
                Arrays.asList(self.getId(), user.getId())));
    }

}
