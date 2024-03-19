package com.ade.chatclient.application;

import com.ade.chatclient.repository.*;
import com.ade.chatclient.repository.impl.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepositoryFactory {
    private final ApiFactory apiFactory;
    private MessageRepository messageRepository;
    private ChatRepository chatRepository;
    private UsersRepository usersRepository;
    private SelfRepository selfRepository;
    private AdminRepository adminRepository;
    private FileRepository fileRepository;

    public ChatRepository provideChatRepository() {
        if (chatRepository == null) {
            chatRepository = new ChatRepositoryImpl(apiFactory.provideChatApi());
        }
        return chatRepository;
    }

    public MessageRepository provideMessageRepository() {
        if (messageRepository == null) {
            messageRepository = new MessageRepositoryImpl(apiFactory.provideMessageApi());
        }
        return messageRepository;
    }

    public UsersRepository provideUsersRepository() {
        if (usersRepository == null) {
            usersRepository = new UsersRepositoryImpl(apiFactory.provideUsersApi());
        }
        return usersRepository;
    }

    public SelfRepository provideSelfRepository() {
        if (selfRepository == null) {
            selfRepository = new SelfRepositoryImpl(apiFactory.provideSelfApi());
        }
        return selfRepository;
    }

    public AdminRepository provideAdminRepository() {
        if (adminRepository == null) {
            adminRepository = new AdminRepositoryImpl(apiFactory.provideAdminApi());
        }
        return adminRepository;
    }

    public FileRepository provideFileRepository() {
        if (fileRepository == null) {
            fileRepository = new FileRepositoryImpl(apiFactory.provideFileApi());
        }

        return fileRepository;
    }
}
