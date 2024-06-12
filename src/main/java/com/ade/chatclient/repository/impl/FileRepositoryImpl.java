package com.ade.chatclient.repository.impl;


import com.ade.chatclient.api.FileApi;
import com.ade.chatclient.repository.FileRepository;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {
    private final FileApi fileApi;
    private final Map<String, byte[]> allFiles = new HashMap<>();

    @Override
    public CompletableFuture<byte[]> getFile(String fileId) {
        if (allFiles.containsKey(fileId)) {
            return CompletableFuture.completedFuture(allFiles.get(fileId));
        }

        return fileApi.fetchImage(fileId).thenApply(file -> {
            allFiles.put(fileId, file);
            return file;
        });
    }

    @Override
    public void clear() {
        allFiles.clear();
    }

    @Override
    public CompletableFuture<String> uploadPhoto(Path path) {
        return fileApi.uploadPhoto(path);
    }
}
