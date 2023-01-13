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

    private Long myChatId;
    private List<Message> MyChatHistory;


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

    //
    @Override
    public List<Long> getMyChatId() {
        if (self == null)
            throw new RuntimeException("attempt to get chats before log in");
        if (myChats == null)
            throw new RuntimeException("attempt to get chats before log in");

        var chatsAsLong = new ArrayList<Long>();
        myChats.forEach((chat) -> {
            chatsAsLong.add(chat.getId());
        });

        return chatsAsLong;
    }

    @Override
    public List<ArrayList<String>> getMyChats() {
        if (self == null)
            throw new RuntimeException("attempt to get chats before log in");

        // myChats is not initialized yet, so it needs to be requested
        if (myChats == null) {

            // a request to server, made through RequestHandler
            myChats = handler.mapResponse(
                    handler.GETRequest(String.format("/users/%d/chats", self.getId())),
                    new TypeReference<>(){}
            );
        }

        var chatsAsStrings = new ArrayList<ArrayList<String>>();
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

    @Override
    public void Authorize(String login) {
        self = handler.mapResponse(
                handler.GETRequest("/user", Map.of("name", login)),
                User.class
        );
    }

    public void initChat(Long id, List<Long> chatsId) {
        if (chatsId.contains(id)){
            //histiry
        }
        else {
            //create
        }
    }
    public Long getMyIdChat() {
        myChatId = (long)1;
        return myChatId;
    }


    public List<Message> getMyChatHistory() {
        if (MyChatHistory == null) {

            MyChatHistory = handler.mapResponse(
                    handler.GETRequest(String.format("/chats/%d/messages", myChatId)),
                    new TypeReference<>(){}
            );
        }
        System.out.print(MyChatHistory);
        var messagesAsStrings = new ArrayList<Message>();
        return messagesAsStrings;
    }


//    public void History(Long chat) {
//        self = handler.mapResponse(
//                handler.GETRequest("/chats", Map.of("name", )),
//                User.class
//        );
//    }
//}

}
