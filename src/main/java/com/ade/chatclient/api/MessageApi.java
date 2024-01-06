package com.ade.chatclient.api;

import com.ade.chatclient.domain.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageApi {
    CompletableFuture<List<Message>> fetchMessagesFromChatById(Long chaId, Long selfId);

    void sendMessageToChat(String text, Long chatId, Long selfId);
}
