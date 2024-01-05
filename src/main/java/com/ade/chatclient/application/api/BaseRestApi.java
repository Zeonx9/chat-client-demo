package com.ade.chatclient.application.api;

import com.ade.chatclient.application.AsyncRequestHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseRestApi {
    protected final AsyncRequestHandler handler;
}
