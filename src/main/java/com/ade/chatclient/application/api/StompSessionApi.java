package com.ade.chatclient.application.api;

import com.ade.chatclient.domain.User;

public interface StompSessionApi {
    void subscribeTopicConnection();
    void subscribeQueueMessages(Long selfId);

    void sendConnectSignal(User user);

    void sendDisconnectSignal(User user);

    void connect();
}
