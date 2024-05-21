package com.ade.chatclient.api;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.dtos.ChangeGroupName;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.dtos.UnreadCounterDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatApi {
    CompletableFuture<List<Chat>> fetchChatsOfUser(Long userId);

    CompletableFuture<Chat> editGroupName(long chatId, ChangeGroupName changeGroupName);

    CompletableFuture<Chat> addUser(long chatId, long userId);

    CompletableFuture<Chat> removeUser(long chatId, long userId);

    void createNewGroupChat(GroupRequest groupRequest);

    CompletableFuture<Chat> createNewPrivateChat(Long selfId, Long userId);

    CompletableFuture<List<UnreadCounterDto>> getListOfUnreadCounters(Long selfId);
}
