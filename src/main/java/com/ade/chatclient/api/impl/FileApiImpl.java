package com.ade.chatclient.api.impl;

import com.ade.chatclient.api.FileApi;
import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FileApiImpl extends BaseRestApi implements FileApi {
    public FileApiImpl(AsyncRequestHandler handler) {
        super(handler);
    }

    @Override
    public CompletableFuture<byte[]> fetchImage(String fileId) {
        return handler.sendGetResource(String.format("/download/%s", fileId));
    }

    @Override
    public CompletableFuture<User> uploadUserPhoto(long userId, Path path) {
        return handler.sendPostResource(String.format("/users/%d/profile_photo", userId), path, User.class);
    }

    @Override
    public CompletableFuture<String> uploadPhoto(Path path) {
        return handler.sendPostResource("/upload", path, String.class);
    }


    @Override
    public CompletableFuture<Chat> uploadGroupChatPhoto(long chatId, Path path) {
        return handler.sendPostResource(String.format("/chats/%d/group_photo", chatId), path, Chat.class);
    }

}
