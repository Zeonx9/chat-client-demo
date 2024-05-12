package com.ade.chatclient.repository.impl;

import com.ade.chatclient.api.ChatApi;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ChangeGroupName;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.dtos.UnreadCounterDto;
import com.ade.chatclient.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {
    private final ChatApi chatApi;
    @Setter
    private Long selfId;
    private Map<Long, Chat> chatById = new HashMap<>();
    private List<Chat> orderedChats = new ArrayList<>();

    @Override
    public CompletableFuture<List<Chat>> fetchChats() {
        if (chatById.isEmpty()) {
            return chatApi.fetchChatsOfUser(selfId)
                    .thenCompose(chats -> chatApi.getListOfUnreadCounters(selfId)
                            .thenApply(counters -> {
                                orderedChats = chats;
                                chatById = chats.stream().collect(Collectors.toMap(Chat::getId, Function.identity()));
                                for (UnreadCounterDto counter : counters) {
                                    if (chatById.containsKey(counter.getChatId())) {
                                        chatById.get(counter.getChatId()).setUnreadCount(counter.getCount());
                                    }
                                }
                                return chats;
                            }));
        } else {
            return CompletableFuture.completedFuture(orderedChats);
        }
    }

    @Override
    public void moveChatUp(Chat chat) {
        orderedChats.remove(chat);
        orderedChats.add(0, chat);
    }

    @Override
    public Chat getChatById(Long chatId) {
        return chatById.get(chatId);
    }

    @Override
    public void clearChats() {
        selfId = null;
        chatById.clear();
        orderedChats.clear();
    }

    @Override
    public List<Chat> getChats() {
        return orderedChats;
    }

    @Override
    public void acceptNewChat(Chat chat) {
        if (chatById.containsKey(chat.getId())) {
            return;
        }
        orderedChats.add(0, chat);
        chatById.put(chat.getId(), chat);
    }

    @Override
    public void createNewGroupChat(GroupRequest groupRequest) {
        chatApi.createNewGroupChat(groupRequest);
    }

    @Override
    public CompletableFuture<Chat> createNewPrivateChat(Long userId) {
        return chatApi.createNewPrivateChat(selfId, userId);
    }

    @Override
    public List<Chat> search(String request) {
        String processedString = processingSearchString(request);
        return orderedChats.stream().filter(chat -> {
            if (chat.getIsPrivate()) {
                User user = chat.getMembers().stream().filter(u -> !Objects.equals(u.getId(), selfId)).toList().get(0);
                return byUserName(user, processedString);
            } else {
                return chat.getGroup().getName().toLowerCase().startsWith(processedString);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<Chat> editGroupName(long chatId, ChangeGroupName changeGroupName) {
        return chatApi.editGroupName(chatId, changeGroupName).thenApply(chat -> {
            updateGroupChatInfo(chatId, chat);
            return chat;
        });
    }

    private void updateGroupChatInfo(long chatId, Chat newChat) {
        chatById.get(chatId).setGroup(newChat.getGroup());
        orderedChats.forEach(chat -> {
            if (chat.getId() == chatId) {
                chat.setGroup(newChat.getGroup());
            }
        });
    }
}
