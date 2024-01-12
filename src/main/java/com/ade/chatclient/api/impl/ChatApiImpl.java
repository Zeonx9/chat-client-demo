package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.BaseRestApi;
import com.ade.chatclient.api.ChatApi;
import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.TypeReferences;
import com.ade.chatclient.dtos.GroupRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatApiImpl extends BaseRestApi implements ChatApi {
    public ChatApiImpl(AsyncRequestHandler handler) {
        super(handler);
    }

    @Override
    public CompletableFuture<List<Chat>> fetchChatsOfUser(Long userId) {
        return handler.sendGet(
                String.format("/users/%d/chats", userId),
                TypeReferences.ListOfChat
        );
    }

//    @Override
//    public CompletableFuture<Chat> fetchChatById(Long userId, Long chatId) {
//        return handler.sendGet(
//                String.format("/chats/%d", chatId),
//                Chat.class
//        );
//    }

    @Override
    public void createNewGroupChat(GroupRequest groupRequest) {
        handler.sendPost(
                "/group_chat",
                groupRequest,
                Chat.class,
                true
        );
    }

    @Override
    public CompletableFuture<Chat> createNewPrivateChat(Long selfId, Long userId) {
        return handler.sendGet(
                String.format("/private_chat/%d/%d", selfId, userId),
                Chat.class
        );
    }
}
