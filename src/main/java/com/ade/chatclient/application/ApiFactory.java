package com.ade.chatclient.application;

import com.ade.chatclient.api.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ApiFactory {
    @Getter
    private final AsyncRequestHandler requestHandler = new AsyncRequestHandler();
    private AuthorizationApi authApi;
    private StompSessionApi stompApi;
    private MessageApi messageApi;
    private ChatApi chatApi;

    public AuthorizationApi provideAuthorizationApi() {
        if (authApi == null) {
            authApi = new AuthorizationApiImpl(requestHandler);
        }
        return authApi;
    }

    public StompSessionApi provideStompSessionApi() {
        if (stompApi == null) {
            stompApi = new StompSessionApiIml();
        }
        return stompApi;
    }

    public MessageApi provideMessageApi() {
        if (messageApi == null) {
            messageApi = new MessageApiImpl(requestHandler);
        }
        return messageApi;
    }

    public ChatApi provideChatApi() {
        if (chatApi == null) {
            chatApi = new ChatApiImpl(requestHandler);
        }
        return chatApi;
    }
}
