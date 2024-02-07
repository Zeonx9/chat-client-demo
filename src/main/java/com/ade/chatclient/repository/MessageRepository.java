package com.ade.chatclient.repository;

import com.ade.chatclient.domain.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageRepository {
    void setSelfId(Long selfId);

    void acceptNewMessage(Message message);

    CompletableFuture<List<Message>> getMessagesOfChat(Long chatId);

    void sendMessageToChat(String text, Long chatId);

    void clear();
}
