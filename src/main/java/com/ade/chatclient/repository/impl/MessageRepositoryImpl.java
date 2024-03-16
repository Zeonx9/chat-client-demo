package com.ade.chatclient.repository.impl;

import com.ade.chatclient.api.MessageApi;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {
    @Setter
    private Long selfId;
    private final MessageApi messageApi;
    private final Map<Long, List<Message>> messagesByChatId = new HashMap<>();

    @Override
    public void acceptNewMessage(Message message) {
        if (!messagesByChatId.containsKey(message.getChatId())) {
            getMessagesOfChat(message.getChatId());
        } else {
            messagesByChatId.get(message.getChatId()).add(message);
        }
    }

    @Override
    public CompletableFuture<List<Message>> getMessagesOfChat(Long chatId) {
        if (!messagesByChatId.containsKey(chatId)) {
            return messageApi.fetchMessagesFromChatById(chatId, selfId).thenApply(messages -> {
                messagesByChatId.put(chatId, messages);
                return messages;
            });
        }
        return CompletableFuture.completedFuture(messagesByChatId.get(chatId));
    }

    @Override
    public void sendMessageToChat(String text, Long chatId) {
        messageApi.sendMessageToChat(text, chatId, selfId);
    }
}
