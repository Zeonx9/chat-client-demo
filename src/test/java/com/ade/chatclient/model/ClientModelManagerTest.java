package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpRequest;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ClientModelManagerTest {
    @Mock private RequestHandler handler;
    private ClientModelManager underTest;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Artem");
        underTest = new ClientModelManager(handler);
        underTest.setMySelf(user);
    }

    @Test
    void updateMyChatsCreatesCorrectRequestAndWorks() {
        //given
        HttpRequest req = RequestHandler.getEmptyReq();
        List<Chat> chats = List.of(new Chat());
        given(handler.GETRequest("/users/1/chats")).willReturn(req);
        given(handler.mapResponse(req, RequestHandler.Types.ListOfChat)).willReturn(chats);

        //when
        underTest.updateMyChats();

        //then
        assertThat(underTest.getMyChats()).isEqualTo(chats);

    }
}