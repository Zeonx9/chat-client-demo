package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BackgroundService {
    private final ClientModel model;
    boolean isActive = false;

    public void run() {
        if (isActive) {
            return;
        }
        System.out.println("Background service started");
        isActive = true;

        // call updates that should be called once
        model.updateAllUsers();
        model.updateMyChats();

        // schedule actions that need to be run in background
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleWithFixedDelay(this::runUpdateMessages, 0, 1000, TimeUnit.MILLISECONDS);
        runAutoUpdateMessages();
    }

    private void runAutoUpdateMessages() {
        Thread thread = new Thread(() -> {
            while (true) {
                model.updateMessages();
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

//    private void runUpdateMessages() {
//        System.out.println("delayed runnable");
//    }
}
