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
        runAutoUpdateMessages();
    }

    private void runAutoUpdateMessages() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(model::updateMessages, 0, 700, TimeUnit.MILLISECONDS);
    }

}
