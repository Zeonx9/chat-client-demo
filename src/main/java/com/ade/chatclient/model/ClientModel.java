package com.ade.chatclient.model;

import com.ade.chatclient.model.domain.Chat;
import com.ade.chatclient.model.domain.Message;
import com.ade.chatclient.model.domain.User;

import java.util.List;

// an interface that a model should implement, used to add flexibility to model structure
public interface ClientModel {

    // returns the data of authorized user
    User getMyself();

    // returns a list of chats where user participates
    List<Chat> getMyChats();

    void updateMyChats();

    //open chat
    void selectChat(Chat chat);

    void updateMessages();

    // method that authorize the user with given login(name)
    boolean Authorize(String login);

    List<Message> getSelectedChatMessages();

    void sendMessageToChat(String text);

    // returns a list of all users
    List<User> getAllUsers();

    void sendMessageToUser(String text, User user);

    void createDialog(User user);
}
