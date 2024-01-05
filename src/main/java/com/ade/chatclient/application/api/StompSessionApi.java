package com.ade.chatclient.application.api;

import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;

import java.util.concurrent.CompletableFuture;

public interface StompSessionApi {
    void subscribeTopicConnection();
    void subscribeTopicMessages();

    void sendConnectSignal(User user);

    void sendDisconnectSignal(User user);

    CompletableFuture<StompSessionApi> connectAsync(String url);

    void sendChatMessage(Message message);
}
