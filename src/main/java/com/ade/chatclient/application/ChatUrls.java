package com.ade.chatclient.application;

import lombok.RequiredArgsConstructor;

import static com.ade.chatclient.application.UrlConstants.*;

@RequiredArgsConstructor
public enum ChatUrls {
    MASTER(SERVER_IP, MASTER_PORT),
    DEV(SERVER_IP, DEV_PORT),
    LOCAL(LOCALHOST, MASTER_PORT);

    private final String ip;
    private final String port;

    public String getServerUrl() {
        return "https://" + ip + ":" + port + ROUTE_PREFIX;
    }

    public String getStompEndpointUrl() {
        return "wss://" + ip + ":" + port + WS_ENDPOINT;
    }
}


