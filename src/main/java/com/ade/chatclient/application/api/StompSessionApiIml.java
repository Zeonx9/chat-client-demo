package com.ade.chatclient.application.api;

import com.ade.chatclient.application.ApplicationStompSessionHandler;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ConnectEvent;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;

import static com.ade.chatclient.application.StartClientApp.URLS;

@Slf4j
public class StompSessionApiIml implements StompSessionApi {
    private final WebSocketStompClient stompClient;
    private StompSession session;

    public StompSessionApiIml() {
        List<Transport> transports = List.of(
                new WebSocketTransport(new StandardWebSocketClient()),
                new RestTemplateXhrTransport()
        );
        WebSocketClient webSocketClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(webSocketClient);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        stompClient.setMessageConverter(converter);
    }

    @Override
    public void connect() {
        if (session != null) {
            throw new IllegalStateException("Attempt to reconnect without closing previous connection");
        }
        try {
            session = stompClient.connectAsync(URLS.getStompEndpointUrl(), new ApplicationStompSessionHandler()).get();
        } catch (Exception e) {
            log.error("failed to get stomp connection", e);
        }
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
    public void subscribeQueueMessages(Long selfId) {
        session.subscribe(String.format("/user/%d/queue/messages", selfId), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Message message = (Message) payload;
                log.info("message received: msg=[text='{}', authorId={}, time={}]", message.getText(), message.getAuthor().getId(), message.getDateTime());
            }
        });
    }

    @Override
    public void sendConnectSignal(User user) {
        session.send("/app/connect", user);
    }

    @Override
    public void sendDisconnectSignal(User user) {
        if (session == null) return;
        session.send("/app/disconnect", user);
        session.disconnect();
    }
}
