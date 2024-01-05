package com.ade.chatclient.application.api;

import com.ade.chatclient.application.ApplicationStompSessionHandler;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ConnectEvent;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
@Slf4j
public class StompSessionApiIml implements StompSessionApi {
    private StompSession session;

    @Override
    public CompletableFuture<StompSessionApi> connectAsync(String url) {
        List<Transport> transports = List.of(
                new WebSocketTransport(new StandardWebSocketClient()),
                new RestTemplateXhrTransport()
        );
        WebSocketClient webSocketClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        stompClient.setMessageConverter(converter);

        StompSessionHandler sessionHandler = new ApplicationStompSessionHandler();
        return stompClient.connectAsync(url, sessionHandler).thenApply(newSession -> {
            session = newSession;
            return this;
        });
    }

    @Override
    public void subscribeTopicConnection() {
        session.subscribe("/topic/connection", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ConnectEvent.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ConnectEvent event = (ConnectEvent) payload;
                log.info("connection event received: {}", event);
            }
        });
    }

    @Override
    public void subscribeTopicMessages() {
        session.subscribe("/topic/public-messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Message message = (Message) payload;
                log.info("public message received: {}", message);
            }
        });
    }

    @Override
    public void sendConnectSignal(User user) {
        session.send("/app/connect", user);
    }

    @Override
    public void sendDisconnectSignal(User user) {
        session.send("/app/disconnect", user);
    }

    @Override
    public void sendChatMessage(Message message) {
        session.send("/app/chat", message);
    }
}
