package com.ade.chatclient.viewmodel;


import com.ade.chatclient.model.ClientModel;
import lombok.RequiredArgsConstructor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Класс, запускающий фоновые сервисы после авторизации
 */
@RequiredArgsConstructor
public class BackgroundService {
    private final ClientModel model;
    static boolean isActive = false;
    private ScheduledExecutorService service;

    /**
     * Запрашивает все чаты авторизованного пользователя и пользователей. Выделяет поток, отвечающий за получение новых сообщений и чатов.
     */
    public void run() {
        if (isActive) {
            return;
        }
//        System.out.println("Background service started");
        isActive = true;

        // call updates that should be called once
        model.fetchUsers();
        model.fetchChats();

        // schedule actions that need to be run in background
        runAutoUpdateMessages();
    }

    private void runAutoUpdateMessages() {
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(model::fetchNewMessages, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Останавливает поток выделенный для получения новых сообщений при закрытии приложения
     */
    public void stop() {
        if (isActive) {
            service.shutdown();
        }
        isActive = false;
    }
}
