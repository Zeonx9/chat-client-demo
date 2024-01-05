package com.ade.chatclient.application;

import com.ade.chatclient.application.api.AuthorizationApi;
import com.ade.chatclient.application.api.AuthorizationApiImpl;
import com.ade.chatclient.application.api.StompSessionApi;
import com.ade.chatclient.application.api.StompSessionApiIml;
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
}
