package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.model.entities.Chat;
import com.ade.chatclient.model.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientModelManager implements ClientModel{
    private final RequestHandler handler;
    private User self;
    private List<Chat> myChats;

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
    public List<String> getMyChats() {
        if (self == null)
            throw new RuntimeException("attempt to get chats before log in");

        // myChats is not initialized yet, so it needs to be requested
        if (myChats == null) {
            myChats = handler.mapResponse(
                    handler.GETRequest(String.format("/users/%d/chats", self.getId())),
                    new TypeReference<>(){}
            );
        }

        var chatsAsStrings = new ArrayList<String>();
        myChats.forEach((chat) -> {
            // chat is represented by its ID and the members except self
            String chatViewStr = chat.getId().toString() + " with: ";
            var memberArray = new ArrayList<String>();
            chat.getMembers().forEach((member) -> {
                if (!Objects.equals(member.getName(), getMyName()))
                    memberArray.add(member.getName());
            });
            chatViewStr += String.join(", ", memberArray);

            chatsAsStrings.add(chatViewStr);
        });
        return chatsAsStrings;
    }

    @Override
    public void Authorize(String login) {
        self = handler.mapResponse(
                handler.GETRequest("/user", Map.of("name", login)),
                User.class
        );
    }
}
