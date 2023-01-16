package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.model.entities.Chat;
import com.ade.chatclient.model.entities.Message;
import com.ade.chatclient.model.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// realization of Client model interface manages and manipulates the data
public class ClientModelManager implements ClientModel{
    private final RequestHandler handler;
    private User self;
    private List<Chat> myChats;

    private List<Long> chatIds;
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
    public List<List<String>> getMyChats() {
        if (self == null)
            throw new RuntimeException("attempt to get chats before log in");
        if (myChats == null) {
            updateMyChats();
        }
        return prepareChatList();
    }

    public void updateMyChats() {
        myChats = handler.mapResponse(
                handler.GETRequest(String.format("/users/%d/chats", self.getId())),
                new TypeReference<>(){}
        );
    }

    private List<List<String>> prepareChatList() {
        var chatsAsStrings = new ArrayList<List<String>>();
        myChats.forEach((chat) -> {
            // chat is represented by its ID and the members except self
            var memberArray = new ArrayList<String>();
            chat.getMembers().forEach((member) -> {
                if (!Objects.equals(member.getName(), getMyName()))
                    memberArray.add(member.getName());
            });
            chatsAsStrings.add(memberArray);
        });

        return chatsAsStrings;
    }

    public List<Long> getMyChatId() {
        if (self == null || myChats == null)
            throw new RuntimeException("attempt to get chats before log in");

        return prepareChatIdList();
    }

    private List<Long> prepareChatIdList() {
        chatIds = new ArrayList<>();
        myChats.forEach((chat) -> chatIds.add(chat.getId()));
        return chatIds;
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
        if (chatIds.contains(chatId)) updateMessages();
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
