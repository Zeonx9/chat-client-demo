package com.ade.chatclient.model;

import com.ade.chatclient.model.entities.Message;

import java.util.ArrayList;
import java.util.List;

// an interface that a model should implement, used to add flexibility to model structure
public interface ClientModel {
    // returns the ID of authorized user
    Long getMyId();

    // returns the name of authorized user
    String getMyName();

    // returns a list of chats where user participates
    List<ArrayList<String>> getMyChats();

    List<Long> getMyChatId();
    Long getMyIdChat();

    List<Message> getMyChatHistory();

    // method that authorize the user with given login(name)
    void Authorize(String login);

    void initChat(Long id, List<Long> chatsId);

}
