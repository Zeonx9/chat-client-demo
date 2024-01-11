package com.ade.chatclient.api;

import com.ade.chatclient.application.ApplicationStompSessionHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ConnectEvent;
import com.ade.chatclient.dtos.ReadNotification;
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
import java.util.function.Consumer;

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

    private <T> StompFrameHandler makeSubscriptionHandler(Class<T> klass, Consumer<T> handler) {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return klass;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                T obj = klass.cast(payload);
                handler.accept(obj);
            }
        };
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
    public void subscribeTopicConnection(Long companyId, Consumer<ConnectEvent> eventConsumer) {
        Consumer<ConnectEvent> handler = (event) -> {
            log.info("connection event received: {}", event);
            eventConsumer.accept(event);
        };
        session.subscribe(
                "/topic/connection",
                makeSubscriptionHandler(ConnectEvent.class, handler)
        );
    }

    @Override
    public void subscribeQueueMessages(Long selfId, Consumer<Message> messageConsumer) {
        Consumer<Message> handler = (message) -> {
            log.info(
                    "message received: msg=[text='{}', authorId={}, time={}]",
                    message.getText(), message.getAuthor().getId(), message.getDateTime()
            );
            messageConsumer.accept(message);
        };
        session.subscribe(
                String.format("/user/%d/queue/messages", selfId),
                makeSubscriptionHandler(Message.class, handler)
        );
    }

    @Override
    public void subscribeQueueChats(Long selfId, Consumer<Chat> chatConsumer) {
        Consumer<Chat> handler = (chat) -> {
            log.info(
                    "new chat was created: chat=[id={}, member_count={}]",
                    chat.getId(), chat.getMembers().size()
            );
            chatConsumer.accept(chat);
        };
        session.subscribe(
                String.format("/user/%d/queue/chats", selfId),
                makeSubscriptionHandler(Chat.class, handler)
        );
    }

    @Override
    public void subscribeQueueReadNotifications(Long selfId, Consumer<ReadNotification> NotificationConsumer) {
        Consumer<ReadNotification> handler = (notification) -> {
            log.info("new read notification received: {}", notification);
            NotificationConsumer.accept(notification);
        };
        session.subscribe(
                String.format("/user/%d/queue/read_notifications", selfId),
                makeSubscriptionHandler(ReadNotification.class, handler)
        );
    }

    @Override
    public void sendConnectSignal(User user) {
        if (session == null) return;
        session.send("/app/connect", user);
    }

    @Override
    public void sendDisconnectSignal(User user) {
        if (session == null) return;
        session.send("/app/disconnect", user);
        session.disconnect();
        session = null;
    }

    @Override
    public void sendReadChatSignal(ReadNotification notification) {
        if (session == null) return;
        session.send("/app/read_chat", notification);
    }
}
