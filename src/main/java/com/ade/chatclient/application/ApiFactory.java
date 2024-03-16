package com.ade.chatclient.application;

import com.ade.chatclient.api.*;
import com.ade.chatclient.api.impl.*;
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
    private UsersApi usersApi;
    private SelfApi selfApi;
    private AdminApi adminApi;

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

    public UsersApi provideUsersApi() {
        if (usersApi == null) {
            usersApi = new UsersApiImpl(requestHandler);
        }
        return usersApi;
    }

    public SelfApi provideSelfApi() {
        if (selfApi == null) {
            selfApi = new SelfApiImpl(requestHandler);
        }
        return selfApi;
    }

    public AdminApi provideAdminApi() {
        if (adminApi == null) {
            adminApi = new AdminApiImpl(requestHandler);
        }
        return adminApi;
    }
}
