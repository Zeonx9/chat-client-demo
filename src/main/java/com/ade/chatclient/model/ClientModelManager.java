package com.ade.chatclient.model;

import com.ade.chatclient.application.AsyncRequestHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.TypeReferences;
import com.ade.chatclient.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// realization of Client model interface manages and manipulates the data
@RequiredArgsConstructor
public class ClientModelManager implements ClientModel{
    private final AsyncRequestHandler handler ;
    @Getter @Setter
    private User myself;
    @Setter
    private Chat selectedChat;
    @Setter
    private List<Chat> myChats = new ArrayList<>();
    @Getter @Setter
    private List<Message> selectedChatMessages = new ArrayList<>();
    @Setter
    private List<User> users = new ArrayList<>();

    /**
     * обновляет чаты пользователя
     * @return все чаты пользователя
     */
    @Override
    public List<Chat> getMyChats() {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in");

        updateMyChats();
        return myChats;
    }

    /**
     * отправляет GET запрос на получение списка всех чатов авторизованного пользователя
     * присваивает полученные чаты в myChats
     */
    @Override
    public void updateMyChats() {

//        myChats = handler.mapResponse(
//                handler.GETRequest(String.format("/users/%d/chats", myself.getId())),
//                RequestHandler.Types.ListOfChat
//        );

        handler.sendGETAsync(String.format("/users/%d/chats", myself.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfChat))
                .thenAccept(this::setMyChats);
    }

    /**
     * отправляет GET запрос на существование пользователя
     * @param login логин пользователя, который хочет авторизоваться
     * @return true - если авторизацция прошла успешно, иначе false
     */
    @Override
    public boolean Authorize(String login) {
        System.out.println("Authorize request: " + login);
        if (myself == null) {
            try {
                //TODO correct тут поменялся енд-поинт, и вообще вот тут оч много че поменялось, надо разбираться
//                myself = handler.mapResponse(
//                        handler.GETRequest("/user", Map.of("name", login)),
//                        User.class
//                );
            }
            catch (Exception e) {
                System.out.println("Authorization failed");
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * присваивает selectedChat значение параметра
     * если чат в есть в списке чатов пользователя
     * @param chat чат, историю которого хотят получить
     */
    @Override
    public void selectChat(Chat chat) {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in or you don't have chats");

        boolean hasSuchChat = false;
        for (Chat myChat : myChats) {
            if (myChat.getId().equals(chat.getId())) {
                hasSuchChat = true;
                break;
            }
        }
        if (!hasSuchChat) {
            System.out.println("User does not has a chat with id: " + chat.getId());
        }
        else setSelectedChat(chat);
    }

    /**
     *  отправляет GET запрос на историю, выбранного чата
     *  присваивает полученные сообщения в selectedChatMessages
     */
    @Override
    public void updateMessages() {
//        selectedChatMessages = handler.mapResponse(
//                handler.GETRequest(String.format("/chats/%d/messages", selectedChat.getId())),
//                RequestHandler.Types.ListOfMessage
//        );
        handler.sendGETAsync(String.format("/chats/%d/messages", selectedChat.getId()))
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfMessage))
                .thenAccept(this::setSelectedChatMessages);

    }

    /**
     * отправляет POST запрос с сообщением в selectedChat
     * @param text сообщение
     */
    @Override
    public void sendMessageToChat(String text) {
//        handler.sendPOST(
//                handler.POSTRequest(
//                        String.format("/users/%d/chats/%d/message", myself.getId(), selectedChat.getId()),
//                        makeBodyForMsgSending(text)
//                )
//        );
        handler.sendPOSTAsync(
                String.format("/users/%d/chats/%d/message", myself.getId(), selectedChat.getId()),
                makeBodyForMsgSending(text),
                true)
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(System.out::println);

    }

    /**
     *
     * @param text сообщение
     * @return сообщение в нужной форме для отправки на сервер
     */
    private String makeBodyForMsgSending(String text) {
        return "{ \"text\": \"" + text + "\" }";
    }

    /**
     * инициализирует список всех пользователей, если он пуст
     * @return список всех пользователей
     */
    @Override
    public List<User> getAllUsers() {
        if (users.isEmpty()) {
            allUsers();
        }
        return users;
    }

    /**
     * отправляет GET запрос на получение списка всех пользователей
     * присваивает полученных пользователей в users
     */
    private void allUsers() {
        if (myself == null)
            throw new RuntimeException("attempt to get chats before log in");

//        users = handler.mapResponse(
//                handler.GETRequest("/users"),
//                RequestHandler.Types.ListOfUser
//        );
        handler.sendGETAsync("/users")
                .thenApply(AsyncRequestHandler.mapperOf(TypeReferences.ListOfUser))
                .thenAccept(this::setUsers);
    }

    /**
     * отправляет POST запрос с сообщением в чат с выбранным пользователем
     * @param text сообщение
     * @param user пользователь, в чат которому надо отправить сообщение
     */
    @Override
    public void sendMessageToUser(String text, User user) {
//        handler.sendPOST(
//                handler.POSTRequest(
//                        String.format("/users/%d/message/users/%d", myself.getId(), user.getId()),
//                        makeBodyForMsgSending(text)
//                )
//        );
        handler.sendPOSTAsync(
                String.format("/users/%d/message/users/%d", myself.getId(), user.getId()),
                makeBodyForMsgSending(text),
                true
                )
                .thenApply(AsyncRequestHandler.mapperOf(Message.class))
                .thenAccept(System.out::println);
    }

    /**
     * отправляет POST запрос на создание личного чата между авторизованными пользователем и выбранным
     * @param user выбранный пользователь
     */
    @Override
    public void createDialog(User user) {
//        handler.sendPOST(
//                handler.POSTRequest(
//                        "/chat?isPrivate=true",
//                        List.of(myself.getId(), user.getId())
//                )
//        );
        handler.sendPOSTAsync(
                "/chat?isPrivate=true",
                List.of(myself.getId(), user.getId()),
                true)
                .thenApply(AsyncRequestHandler.mapperOf(Chat.class))
                .thenAccept(System.out::println);
    }

}
