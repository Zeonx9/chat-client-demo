package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.MessageApi;
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
    public void sendMessageToChat(String text, Long chatId, Long selfId, String attachment) {
        String url = "/users/%d/chats/%d/message";
        if (attachment != null) {
            url += "?attachment=" + attachment;
        }
        handler.sendPost(
                String.format(url, selfId, chatId),
                Message.builder().text(text).dateTime(LocalDateTime.now()).build(),
                Message.class, true
        );
    }
}
