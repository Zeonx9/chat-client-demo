package com.ade.chatclient.api;

import java.util.concurrent.CompletableFuture;

public interface FileApi {
    CompletableFuture<byte[]> fetchImage(String fileId);
}
