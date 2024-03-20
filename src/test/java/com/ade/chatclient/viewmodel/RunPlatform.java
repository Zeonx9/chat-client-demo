package com.ade.chatclient.viewmodel;

import javafx.application.Platform;

public class RunPlatform {
    static {
        try {
            Platform.startup(() -> {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
