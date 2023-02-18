package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BackgroundService {
    private final ClientModel model;
    boolean isActive = false;

    public void run() {
        if (isActive) {
            return;
        }
        System.out.println("Background service started");
        model.updateMyChats();
        model.updateAllUsers();
        isActive = true;
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
}
