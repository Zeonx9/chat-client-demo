package com.ade.chatclient.repository;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.dtos.ChangeGroupName;
import com.ade.chatclient.dtos.GroupRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatRepository extends Searchable<Chat>{
    CompletableFuture<List<Chat>> fetchChats();

    void moveChatUp(Chat chat);

    Chat getChatById(Long chatId);

    void clearChats();

    List<Chat> getChats();

    void acceptNewChat(Chat chat);

    void createNewGroupChat(GroupRequest groupRequest);

    CompletableFuture<Chat> createNewPrivateChat(Long userId);

    void setSelfId(Long selfId);

    CompletableFuture<Chat> editGroupName(long chatId, ChangeGroupName changeGroupName);
}
