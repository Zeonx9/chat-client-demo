package com.ade.chatclient.application;

import com.ade.chatclient.repository.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepositoryFactory {
    private final ApiFactory apiFactory;
    private MessageRepository messageRepository;
    private ChatRepository chatRepository;
    private UsersRepository usersRepository;
    private SelfRepository selfRepository;
    private AdminRepository adminRepository;

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

    public UsersRepository provideUsersRepository() {
        if (usersRepository == null) {
            usersRepository = new UsersRepository(apiFactory.provideUsersApi());
        }
        return usersRepository;
    }

    public SelfRepository provideSelfRepository() {
        if (selfRepository == null) {
            selfRepository = new SelfRepository(apiFactory.provideSelfApi());
        }
        return selfRepository;
    }

    public AdminRepository provideAdminRepository() {
        if (adminRepository == null) {
            adminRepository = new AdminRepository(apiFactory.provideAdminApi());
        }
        return adminRepository;
    }
}
