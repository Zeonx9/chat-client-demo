package com.ade.chatclient.api;

import com.ade.chatclient.domain.User;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface FileApi {
    CompletableFuture<byte[]> fetchImage(String fileId);

    CompletableFuture<User> uploadUserPhoto(long userId, Path path);

    CompletableFuture<String> uploadPhoto(Path path);
}
