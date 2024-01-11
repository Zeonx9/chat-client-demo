package com.ade.chatclient.api;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.TypeReferences;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.dtos.UnreadCounterDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatApiImpl extends BaseRestApi implements ChatApi{
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

    @Override
    public CompletableFuture<List<UnreadCounterDto>> getListOfUnreadCounters(Long selfId) {
        return handler.sendGet(
                String.format("/users/%d/chats/unread", selfId),
                TypeReferences.ListOfUnreadCounters
        );
    }
}
