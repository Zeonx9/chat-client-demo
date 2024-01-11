package com.ade.chatclient.api;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ConnectEvent;
import com.ade.chatclient.dtos.ReadNotification;

import java.util.function.Consumer;

public interface StompSessionApi {
    void connect();
    void subscribeTopicConnection(Long companyId, Consumer<ConnectEvent> handler);
    void subscribeQueueMessages(Long selfId, Consumer<Message> handler);
    void subscribeQueueChats(Long selfId, Consumer<Chat> handler);
    void subscribeQueueReadNotifications(Long selfId, Consumer<ReadNotification> handler);
    void sendConnectSignal(User user);
    void sendDisconnectSignal(User user);

    void sendReadChatSignal(ReadNotification notification);
}
