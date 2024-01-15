package com.ade.chatclient.api.impl;

import com.ade.chatclient.application.AsyncRequestHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseRestApi {
    protected final AsyncRequestHandler handler;
}
