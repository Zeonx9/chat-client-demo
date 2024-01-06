package com.ade.chatclient.application;

import com.ade.chatclient.repository.ChatRepository;
import com.ade.chatclient.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepositoryFactory {
    private final ApiFactory apiFactory;
    private MessageRepository messageRepository;
    private ChatRepository chatRepository;

    public ChatRepository provideChatRepository() {
        if (chatRepository == null) {
            chatRepository = new ChatRepository(apiFactory.provideChatApi());
        }
        return chatRepository;
    }

    public MessageRepository provideMessageRepository() {
        if (messageRepository == null) {
            messageRepository = new MessageRepository(apiFactory.provideMessageApi());
        }
        return messageRepository;
    }
}
