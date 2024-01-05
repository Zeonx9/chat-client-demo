package com.ade.chatclient.application;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

@NoArgsConstructor
@Slf4j
public class ApplicationStompSessionHandler extends StompSessionHandlerAdapter  {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Connection is ready, Stomp Session id: {}, destination: {}", session.getSessionId(), connectedHeaders.getDestination());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("caught exception in stomp session", exception);
    }
}
