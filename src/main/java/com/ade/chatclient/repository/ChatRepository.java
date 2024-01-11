package com.ade.chatclient.repository;

import com.ade.chatclient.api.ChatApi;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.dtos.GroupRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChatRepository {
    private final ChatApi chatApi;
    @Setter
    private Long selfId;
    private Map<Long, Chat> chatById = new HashMap<>();
    private List<Chat> orderedChats = new ArrayList<>();

    public CompletableFuture<List<Chat>> fetchChats() {
        if (chatById.isEmpty()) {
            var futureCounters = chatApi.getListOfUnreadCounters(selfId);
            return chatApi.fetchChatsOfUser(selfId).thenCombine(futureCounters, (chats, counters) -> {
                orderedChats = chats;
                chatById = chats.stream().collect(Collectors.toMap(Chat::getId, Function.identity()));
                for (var counter : counters) {
                    chatById.get(counter.getChatId()).setUnreadCount(counter.getCount());
                }
                return chats;
            });
        } else {
            return CompletableFuture.completedFuture(orderedChats);
        }
    }

    public void moveChatUp(Chat chat) {
        orderedChats.remove(chat);
        orderedChats.add(0, chat);
    }

    public Chat getChatById(Long chatId) {
        return chatById.get(chatId);
    }

    public void clearChats() {
        chatById.clear();
        orderedChats.clear();
    }

    public List<Chat> getChats() {
        return orderedChats;
    }

    public void acceptNewChat(Chat chat) {
        if (chatById.containsKey(chat.getId())) {
            return;
        }
        orderedChats.add(0, chat);
        chatById.put(chat.getId(), chat);
    }

    public void createNewGroupChat(GroupRequest groupRequest) {
        chatApi.createNewGroupChat(groupRequest);
    }

    public CompletableFuture<Chat> createNewPrivateChat(Long userId) {
        return chatApi.createNewPrivateChat(selfId, userId);
    }
}
