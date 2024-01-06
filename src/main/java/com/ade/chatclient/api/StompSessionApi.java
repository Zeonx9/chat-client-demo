package com.ade.chatclient.api;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;

import java.util.function.Consumer;

public interface StompSessionApi {
    void connect();
    void subscribeTopicConnection();
    void subscribeQueueMessages(Long selfId, Consumer<Message> handler);
    void subscribeQueueChats(Long selfId, Consumer<Chat> handler);
    void sendConnectSignal(User user);
    void sendDisconnectSignal(User user);
}
