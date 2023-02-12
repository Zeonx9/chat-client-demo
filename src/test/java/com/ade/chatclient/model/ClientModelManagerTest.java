package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpRequest;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * класс, который реализует Mock/Junit тесты для Модели
 * модель зависит от RequestHandler, нужно сделать мок для него
 * тестирует поведение модели, при условии, что запросы прошли так, как мы хотели
 */
@ExtendWith(MockitoExtension.class)
class ClientModelManagerTest {
    @Mock private RequestHandler handler;
    private ClientModelManager underTest;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Artem");
        List <User> users = List.of(user, new User(2L, "Dasha"));
        underTest = new ClientModelManager(handler);
        underTest.setMySelf(user);
        Chat chat = new Chat(1L, true, users);
        underTest.setSelectedChat(chat);
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

    @Test
    void getAllUsersCreatesCorrectRequestAndWorks() {
        //given
        HttpRequest req = RequestHandler.getEmptyReq();
        List<User> users = List.of(new User());
        given(handler.GETRequest("/users")).willReturn(req);
        given(handler.mapResponse(req, RequestHandler.Types.ListOfUser)).willReturn(users);

        //when
        underTest.getAllUsers();

        //then
        assertThat(underTest.getAllUsers()).isEqualTo(users);

    }

    @Test
    void willThrownWhenMyselfIsNull() {
        //given
        underTest.setMySelf(null);

        //when
        //then
        assertThatThrownBy(() -> underTest.getMyChats())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("attempt to get chats before log in");

    }

    @Test
    void AuthorizeAuthorizeCreatesCorrectRequestAndWorks() {
        //given
        underTest.setMySelf(null);

        //when
        underTest.Authorize("Artem");

        //given
        assertTrue(underTest.Authorize("Artem"));

    }

    @Test
    void getSelectedChatMessagesCreatesCorrectRequestAndWorks() {
        //given
        HttpRequest req = RequestHandler.getEmptyReq();
        List<Message> mes = List.of(new Message());
        given(handler.GETRequest(String.format("/chats/%d/messages", 1L))).willReturn(req);
        given(handler.mapResponse(req, RequestHandler.Types.ListOfMessage)).willReturn(mes);

        //when
        underTest.updateMessages();

        //then
        assertThat(underTest.getSelectedChatMessages()).isEqualTo(mes);
    }

}