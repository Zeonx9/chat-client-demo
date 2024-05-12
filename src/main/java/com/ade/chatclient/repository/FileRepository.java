package com.ade.chatclient.repository;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface FileRepository {
    CompletableFuture<byte[]> getFile(String fileId);

    void clear();

    CompletableFuture<String> uploadPhoto(Path path);
}
