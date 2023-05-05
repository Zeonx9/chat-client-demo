package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.*;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.GroupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * класс, который реализует Mock/Junit тесты для Модели
 * модель зависит от RequestHandler, нужно сделать мок для него
 * тестирует поведение модели, при условии, что запросы прошли так, как мы хотели
 */
@ExtendWith(MockitoExtension.class)
class ClientModelImplTest {
    @Mock private AsyncRequestHandler handler;
    private ClientModelImpl underTest ;

    @BeforeEach
    void setUp() {
        underTest = new ClientModelImpl(handler);
        User myself = User.builder().username("Dasha").id(1L).build();
        underTest.setMyself(myself);
        User user = User.builder().username("Egor").id(1L).build();
        Chat chat = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user)).build();
        underTest.setSelectedChat(chat);
        underTest.setCompany(Company.builder().id(1L).build());
    }

    @Test
    void createDialogFromAllUsers(){
        //given
        User user = User.builder().username("Egor").id(2L).build();
        Chat expectedChat = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user)).build();
        CompletableFuture<Chat> chat  = CompletableFuture.completedFuture(expectedChat);
        given(handler.sendGet(String.format("/private_chat/%d/2", underTest.getMyself().getId()), Chat.class))
                .willReturn(chat);

        //when
        Chat chat_ = underTest.createDialogFromAllUsers(user);

        //then
        assertThat(chat_).isEqualTo(expectedChat);

    }

    @Test
    public void SearchUser() {
        //given
        User user = User.builder().username("Egor").id(2L).build();
        List<User> allUsers = List.of(user, underTest.getMyself());
        underTest.setAllUsers(allUsers);
        List<User> expectedList = List.of(user);

        //when
        List<User> returned = underTest.searchUser("Egor");

        //then
        assertThat(returned).isEqualTo(expectedList);
    }

    @Test
    public void SearchChatForExistingChat() {
        //given
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user1)).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(true).members(List.of(underTest.getMyself(), user2)).build();
        underTest.setMyChats(List.of(chat1, chat2));
        List<Chat> expectedList = List.of(chat1);

        //when
        List<Chat> returned = underTest.searchChat("Egor");

        //then
        assertThat(returned).isEqualTo(expectedList);
    }

    @Test
    public void SearchChatForExistingGroup() {
        //given
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(false).members(List.of(underTest.getMyself(), user1, user2))
                .group(GroupChatInfo.builder().name("Group1").build()).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(false).members(List.of(underTest.getMyself(), user2, user1))
                .group(GroupChatInfo.builder().name("Group2").build()).build();
        underTest.setMyChats(List.of(chat1, chat2));
        List<Chat> expectedList = List.of(chat1);

        //when
        List<Chat> returned = underTest.searchChat("Group1");

        //then
        assertThat(returned).isEqualTo(expectedList);
    }

    @Test
    public void SearchChatForNonExistingChat() {
        //given
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user1)).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(true).members(List.of(underTest.getMyself(), user2)).build();
        underTest.setMyChats(List.of(chat1, chat2));

        //when
        List<Chat> returned = underTest.searchChat("Max");

        //then
        assertThat(returned).isNotEqualTo(List.of(chat1));
        assertThat(returned).isNotEqualTo(List.of(chat2));
        assertThat(returned).isNotEqualTo(List.of(chat1, chat2));
    }

    @Test
    void createGroupChat(){
        //given
        User user = User.builder().username("Artem").id(2L).build();
        Chat expectedChat = Chat.builder().id(1L).isPrivate(false).members(List.of(underTest.getMyself(), user)).build();
        GroupRequest groupRequest = GroupRequest.builder().ids(new ArrayList<>(List.of(2L)))
                .groupInfo(GroupChatInfo.builder().name("Group").creator(underTest.getMyself()).build()).build();
        CompletableFuture<Chat> chat  = CompletableFuture.completedFuture(expectedChat);
        given(handler.sendPost("/group_chat", groupRequest, Chat.class, true))
                .willReturn(chat);

        //then
        assertDoesNotThrow(() -> underTest.createGroupChat(groupRequest));

    }

    @Test
    void fetchNewMessages(){
        //given
        User user = User.builder().username("Egor").id(2L).build();
        Message message = Message.builder().id(1L).text("hello").author(user).chatId(1L).build();
        List<Message> mes = new ArrayList<>(List.of(message));
        CompletableFuture<List<Message>> messages = CompletableFuture.completedFuture(mes);

        given(handler.sendGet(String.format("/users/%d/undelivered_messages", underTest.getMyself().getId()), TypeReferences.ListOfMessage))
                .willReturn(messages);

        underTest.fetchNewMessages();
        verify(handler).sendGet("/users/1/undelivered_messages", TypeReferences.ListOfMessage);
    }


    @Test
    void sendMessageToChat() {
        User user = User.builder().username("User").id(2L).build();
        Message message_ = Message.builder().text("hello").author(user).chatId(1L).build();
        CompletableFuture<Message> message = CompletableFuture
                .completedFuture(message_);
        given(handler.sendPost(
                String.format("/users/%d/chats/%d/message", underTest.getMyself().getId(), underTest.getSelectedChat().getId()),
                Message.builder().text("hello").build(),
                Message.class, true
        )).willReturn(message);

        underTest.sendMessageToChat("hello");

        assertThat(underTest.getSelectedChat().getLastMessage()).isEqualTo(message);
    }

    @Test
    void fetchChatMessages(){
        User user = User.builder().username("user").id(2L).build();
        Message message1 = Message.builder().text("hello").author(user).chatId(1L).build();
        Message message2 = Message.builder().text("bye").author(user).chatId(1L).build();

        List<Message> users = new ArrayList<>(List.of(message1, message2));
        CompletableFuture<List<Message>> messages = CompletableFuture.completedFuture(users);
        given(handler.sendGet("/chats/1/messages",
                Map.of("userId", underTest.getMyself().getId().toString()),
                TypeReferences.ListOfMessage
        ))
                .willReturn(messages);

        underTest.fetchChatMessages();
        verify(handler).sendGet("/chats/1/messages",
                Map.of("userId", underTest.getMyself().getId().toString()),
                TypeReferences.ListOfMessage
        );
    }

    @Test
    void fetchUsers() {
        User user = User.builder().username("Egor").id(2L).build();
        List<User> users = new ArrayList<>(List.of(user));
        CompletableFuture<List<User>> allUsers = CompletableFuture.completedFuture(users);
        given(handler.sendGet("/company/" + underTest.getCompany().getId() + "/users", TypeReferences.ListOfUser))
                .willReturn(allUsers);

        underTest.setAllUsers(null);
        underTest.fetchUsers();

        assertThat(underTest.getAllUsers()).isEqualTo(users);
    }
    @Test
    void fetchChats(){
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user1)).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(true).members(List.of(underTest.getMyself(), user2)).build();
        List<Chat> chats = new ArrayList<>(List.of(chat1, chat2));

        CompletableFuture<List<Chat>> myChats = CompletableFuture.completedFuture(chats);
        given(handler.sendGet("/users/1/chats", TypeReferences.ListOfChat))
                .willReturn(myChats);

        underTest.setMyChats(null);
        underTest.fetchChats();

        assertThat(underTest.getMyChats()).isEqualTo(chats);

    }

    @Test
    void AuthorizeLogin() {
        User myself = User.builder().username("Dasha").id(1L).build();
        CompletableFuture<AuthResponse> auth = CompletableFuture.completedFuture(AuthResponse.builder().user(myself).build());
        given(handler.sendPost(
                "/auth/" + "login",
                AuthRequest.builder().login("Dasha").password("password").build(),
                AuthResponse.class, false
        )).willReturn(auth);

        underTest.setMyself(null);
        underTest.authorize("Dasha", "password");

        assertThat(underTest.getMyself()).isEqualTo(myself);

    }

    @Test
    void AuthorizeRegister() {
        User myself = User.builder().username("Dasha").id(1L).build();
        CompletableFuture<AuthResponse> auth = CompletableFuture.completedFuture(AuthResponse.builder().user(myself).build());
        given(handler.sendPost(
                "/auth/" + "register",
                AuthRequest.builder().login("Dasha").password("password").build(),
                AuthResponse.class, false
        )).willReturn(auth);

        underTest.setMyself(null);
        underTest.authorize("Dasha", "password");

        assertThat(underTest.getMyself()).isEqualTo(myself);

    }

    @Test
    void AuthorizeRe() {
        User myself = User.builder().username("Dasha").id(1L).build();

        underTest.authorize("Dasha", "password");

        assertThat(underTest.getMyself()).isEqualTo(myself);

    }

}