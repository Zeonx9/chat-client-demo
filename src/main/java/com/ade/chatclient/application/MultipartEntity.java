package com.ade.chatclient.application;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultipartEntity {
    private final String boundary;
    @Getter
    private String contentType;
    private final Path path;

    public MultipartEntity(Path path) {
        boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        contentType = "multipart/form-data; boundary=" + boundary;
        this.path = path;
    }

    public byte[] photoToByteArray() {
        try {
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            body.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + path.getFileName().toString() + "\"\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(("Content-Type: image/jpeg\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(Files.readAllBytes(path));
            body.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            return body.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
