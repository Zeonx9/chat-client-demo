package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.model.entities.Chat;
import com.ade.chatclient.model.entities.Message;
import com.ade.chatclient.model.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// realization of Client model interface manages and manipulates the data
public class ClientModelManager implements ClientModel{
    private final RequestHandler handler;
    private User self;
    private List<Chat> myChats;
    private List<Message> selectedChatMessages;
    private Long chatId;


    public ClientModelManager(RequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public Long getMyId() {
        if (self == null)
            throw new RuntimeException("attempt to get Id of user before log in");
        return self.getId();
    }

    @Override
    public String getMyName() {
        if (self == null)
            throw new RuntimeException("attempt to get Id of user before log in");
        return self.getName();
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

    public void openChat(Long id) {
        if (self == null) return;
        chatId = id;
        if (myChats == null)
            return;
        boolean hasSuchChat = false;
        for (Chat chat : myChats) {
            if (chat.getId().equals(id)) {
                hasSuchChat = true;
                break;
            }
        }
        if (hasSuchChat)
            updateMessages();
        else
            System.out.println("User does not has a chat with id: " + id);
    }

    //изменить на POST
    public void createChat() {
        selectedChatMessages = handler.mapResponse(
                handler.GETRequest("/chat",Map.of("isPrivate", "true")),
                new TypeReference<>() {
                }
        );
    }

    public void updateMessages() {
        selectedChatMessages = handler.mapResponse(
                handler.GETRequest(String.format("/chats/%d/messages", chatId)),
                new TypeReference<>() {
                });
        if (selectedChatMessages ==null){
            createChat();
        }
    }

    public List<String[]> getSelectedChatMessages() {
        return prepareListMessages();
    }

    private List<String[]> prepareListMessages() {
        var messageAsStrings = new ArrayList<String[]>();
        selectedChatMessages.forEach((mes) -> {
            // chat is represented by its ID and the members except self
            String[] message = new String[4];
            message[0] = mes.getId().toString();
            message[1] = mes.getText();
            message[2] = mes.getDateTime().toString();
            message[3] = mes.getAuthor().getName();
            messageAsStrings.add(message);
        });
        return messageAsStrings;
    }
}
