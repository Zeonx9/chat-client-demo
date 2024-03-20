package com.ade.chatclient.model;

import com.ade.chatclient.api.StompSessionApi;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.impl.ClientModelImpl;
import com.ade.chatclient.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

/**
 * класс, который реализует Mock/Junit тесты для Модели
 * модель зависит от RequestHandler, нужно сделать мок для него
 * тестирует поведение модели, при условии, что запросы прошли так, как мы хотели
 */
public class ClientModelImplTest {

    private ClientModelImpl clientModel;
    private StompSessionApi stompSessionApiMock;
    private MessageRepository messageRepositoryMock;
    private ChatRepository chatRepositoryMock;
    private UsersRepository usersRepositoryMock;
    private SelfRepository selfRepositoryMock;
    private FileRepository fileRepositoryMock;

    @BeforeEach
    public void setUp() {
        stompSessionApiMock = Mockito.mock(StompSessionApi.class);
        messageRepositoryMock = Mockito.mock(MessageRepository.class);
        chatRepositoryMock = Mockito.mock(ChatRepository.class);
        usersRepositoryMock = Mockito.mock(UsersRepository.class);
        selfRepositoryMock = Mockito.mock(SelfRepository.class);
        fileRepositoryMock = Mockito.mock(FileRepository.class);

        clientModel = new ClientModelImpl(stompSessionApiMock, messageRepositoryMock, chatRepositoryMock, usersRepositoryMock, selfRepositoryMock, fileRepositoryMock);
        when(selfRepositoryMock.getMyself()).thenReturn(User.builder().id(1L).username("login").build());
        when(selfRepositoryMock.getCompany()).thenReturn(Company.builder().id(1L).build());
    }

    @Test
    public void testClearModel() {
        clientModel.clearModel();

        verify(chatRepositoryMock).clearChats();
        verify(usersRepositoryMock).clearUsers();
        verify(messageRepositoryMock).clear();
        verify(selfRepositoryMock).clear();
        verify(fileRepositoryMock).clear();
    }

    @Test
    public void testFetchChats() {
        when(chatRepositoryMock.fetchChats()).thenReturn(CompletableFuture.completedFuture(List.of()));

        clientModel.fetchChats();

        verify(chatRepositoryMock).fetchChats();
    }


    @Test
    public void testFetchUsers() {
        when(usersRepositoryMock.fetchUsers(anyLong())).thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        clientModel.fetchUsers();
        verify(usersRepositoryMock).fetchUsers(anyLong());
    }

}


/*
@ExtendWith(MockitoExtension.class)
class ClientModelImplTest {
    @Mock private AsyncRequestHandler handler;
    @Mock private  StompSessionApi stompSessionApi;
    @Mock private MessageRepository messageRepository;
    @Mock private ChatRepository chatRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private SelfRepository selfRepository;
    @Mock private FileRepository fileRepository;
    private ClientModelImpl underTest ;

    @BeforeEach
    void setUp() {
        underTest = new ClientModelImpl( stompSessionApi,messageRepository,chatRepository,usersRepository,selfRepository,fileRepository);
        User myself = User.builder().username("Dasha").realName("Dasha").surname("Vav").id(1L).build();
//        underTest.setMyself(myself);
        User user = User.builder().username("Egor").id(1L).build();
        Chat chat = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user)).unreadCount(0).build();
        underTest.setSelectedChat(chat);
//        underTest.setCompany(Company.builder().id(1L).build());
    }

    @Test
    void createDialogFromAllUsers(){
        //given
        User user = User.builder().username("Egor").id(2L).build();
        Chat expectedChat = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user)).unreadCount(0).build();
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
        User user = User.builder().username("Egor").realName("Egor").surname("Dem").id(2L).build();
        List<User> allUsers = List.of(user, underTest.getMyself());
//        underTest.setAllUsers(allUsers);
        List<User> expectedList = List.of(user);

        //when
        List<User> returned = underTest.searchUser("Egor");

        //then
        assertThat(returned).isEqualTo(expectedList);
    }

    @Test
    public void SearchChatForExistingChat() {
        //given
        User user1 = User.builder().username("Egor").realName("Egor").surname("Dem").id(2L).build();
        User user2 = User.builder().username("Artem").realName("Artem").surname("My").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user1)).unreadCount(0).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(true).members(List.of(underTest.getMyself(), user2)).unreadCount(0).build();
//        underTest.setMyChats(List.of(chat1, chat2));
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
                .group(GroupChatInfo.builder().name("Group1").build()).unreadCount(0).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(false).members(List.of(underTest.getMyself(), user2, user1))
                .group(GroupChatInfo.builder().name("Group2").build()).unreadCount(0).build();
//        underTest.setMyChats(List.of(chat1, chat2));
        List<Chat> expectedList = List.of(chat1);

        //when
        List<Chat> returned = underTest.searchChat("Group1");

        //then
        assertThat(returned).isEqualTo(expectedList);
    }

    @Test
    public void SearchChatForNonExistingChat() {
        //given
        User user1 = User.builder().username("Egor").realName("Egor").surname("Dem").id(2L).build();
        User user2 = User.builder().username("Artem").realName("Artem").surname("My").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user1)).unreadCount(0).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(true).members(List.of(underTest.getMyself(), user2)).unreadCount(0).build();
//        underTest.setMyChats(List.of(chat1, chat2));

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
        Chat expectedChat = Chat.builder().id(1L).isPrivate(false).members(List.of(underTest.getMyself(), user)).unreadCount(0).build();
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

//        underTest.fetchNewMessages();
        verify(handler).sendGet("/users/1/undelivered_messages", TypeReferences.ListOfMessage);
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

        underTest.setSelectChat(Chat.builder().id(1L).unreadCount(0).build());
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

//        underTest.setAllUsers(null);
        underTest.fetchUsers();

        assertThat(underTest.getAllUsers()).isEqualTo(users);
    }

    @Test
    void fetchChats(){
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        Chat chat1 = Chat.builder().id(1L).isPrivate(true).members(List.of(underTest.getMyself(), user1)).unreadCount(0).build();
        Chat chat2 = Chat.builder().id(2L).isPrivate(true).members(List.of(underTest.getMyself(), user2)).unreadCount(0).build();
        List<Chat> chats = new ArrayList<>(List.of(chat1, chat2));

        CompletableFuture<List<Chat>> myChats = CompletableFuture.completedFuture(chats);
        given(handler.sendGet("/users/1/chats", TypeReferences.ListOfChat))
                .willReturn(myChats);

//        underTest.setMyChats(null);
        underTest.fetchChats();

//        assertThat(underTest.getMyChats()).isEqualTo(chats);

    }

    @Test
    void  changePassword(){
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .authRequest(AuthRequest.builder().login("Dasha").password("old").companyId(1L).build())
                .newPassword("new").build();

        AuthResponse response = AuthResponse.builder().user(underTest.getMyself()).isAdmin(false).build();
        CompletableFuture<AuthResponse> expected = CompletableFuture.completedFuture(response);
        given(handler.sendPut("/auth/user/password", request, AuthResponse.class))
                .willReturn(expected);

        underTest.changePassword(request);

        verify(handler).sendPut("/auth/user/password", request, AuthResponse.class);

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
    void registerUser() {
        AuthRequest auth = AuthRequest.builder().login("login").build();
        RegisterData data = RegisterData.builder().authRequest(auth).build();
        CompletableFuture<AuthRequest> authFuture = CompletableFuture.completedFuture(auth);
        given(handler.sendPost("/auth/register", data, AuthRequest.class, true)).willReturn(authFuture);

        AuthRequest result = underTest.registerUser(data);

        assertEquals(result, auth);
    }

    @Test
    void AuthorizeRegister() {
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
    void AuthorizeRe() {
        User myself = User.builder().username("Dasha").id(1L).build();

        underTest.authorize("Dasha", "password");

        assertThat(underTest.getMyself()).isEqualTo(myself);

    }

    @Test
    void clearModel() {

        underTest.clearModel();

        assertThat(underTest.getMyself()).isEqualTo(null);
        assertThat(underTest.getSelectedChat()).isEqualTo(null);
        assertThat(underTest.getCompany()).isEqualTo(null);
//        assertTrue(underTest.getMyChats().isEmpty());
        assertTrue(underTest.getAllUsers().isEmpty());

    }
} */