package com.ade.chatclient.api;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.TypeReferences;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessageApiImpl extends BaseRestApi implements MessageApi {
    public MessageApiImpl(AsyncRequestHandler handler) {
        super(handler);
    }

    @Override
    public CompletableFuture<List<Message>> fetchMessagesFromChatById(Long chatId, Long selfId) {
        return handler.sendGet(
                String.format("/chats/%d/messages", chatId),
                Map.of("userId", selfId.toString()),
                TypeReferences.ListOfMessage
        );
    }

    @Override
    public void sendMessageToChat(String text, Long chatId, Long selfId) {
        handler.sendPost(
                String.format("/users/%d/chats/%d/message", selfId, chatId),
                Message.builder().text(text).dateTime(LocalDateTime.now()).build(),
                Message.class, true
        );
    }
}
