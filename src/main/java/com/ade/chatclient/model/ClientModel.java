package com.ade.chatclient.model;

import com.ade.chatclient.model.entities.Chat;

import java.util.List;

// an interface that a model should implement, used to add flexibility to model structure
public interface ClientModel {
    // returns the ID of authorized user
    Long getMyId();

    // returns the name of authorized user
    String getMyName();

    // returns a list of chats where user participates
    List<Chat> getMyChats();

    void updateMyChats();

    void openChat(Long id);
    void createChat();
    void updateMessages();

    // method that authorize the user with given login(name)
    boolean Authorize(String login);

    List<String[]>  getSelectedChatMessages();
}
