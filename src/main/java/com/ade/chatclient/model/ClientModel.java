package com.ade.chatclient.model;

import com.ade.chatclient.model.entities.Message;

import java.util.List;

// an interface that a model should implement, used to add flexibility to model structure
public interface ClientModel {
    // returns the ID of authorized user
    Long getMyId();

    // returns the name of authorized user
    String getMyName();

    // returns a list of chats where user participates
    List<List<String>> getMyChats();

    void updateMyChats();

    void openChat(Long id);
    void createChat();
    void updateMessages();
    List<Long> getMyChatId();

    // method that authorize the user with given login(name)
    void Authorize(String login);

    List<String[]>  getSelectedChatMessages();
}
