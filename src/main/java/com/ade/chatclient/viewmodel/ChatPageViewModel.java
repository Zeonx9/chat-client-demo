package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ChatPageViewModel {
    // первые 3 поля тоже должен был добавить Егор
    private final ListProperty<Chat> chatListProperty;
    private final ListProperty<Message> messageListProperty;
    private final StringProperty messageTextProperty;


    private final ClientModel model;
    private final ViewHandler viewHandler;


    // конструктор тоже егор

    ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;

        chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
        messageTextProperty = new SimpleStringProperty();
    }

    // методы до следующего коммента - это Даша делает

    public void updateChatList() {
        chatListProperty.clear();
        chatListProperty.addAll(model.getMyChats());
    }

    public void updateMessagesInSelectedChat() {
        model.updateMessages();
        messageListProperty.clear();
        messageListProperty.addAll(model.getSelectedChatMessages());
    }

    public void onSelectedItemChange(Observable observable, Chat oldValue, Chat newValue) {
        model.selectChat(newValue);
        updateMessagesInSelectedChat();
    }

    public void sendMessage() {
        if (messageTextProperty.get().isBlank())
            return;

        model.sendMessageToChat(messageTextProperty.get());
        messageTextProperty.set("");
        updateMessagesInSelectedChat();
    }

    // все метод ниже должен был написать егор

    private String prepareChatToBeShown(Chat chat) {
        List<String> memberNames = new ArrayList<>();
        chat.getMembers().forEach(member -> {
            if (!Objects.equals(member.getId(), model.getMyself().getId()))
                memberNames.add(member.getName());
        });
        return String.join(", ", memberNames);
    }

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return "from " + msg.getAuthor().getName() +
                ": " + msg.getText() +
                " at " + msg.getDateTime().format(dtf);
    }

    public ListCell<Chat> getChatListCellFactory() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(prepareChatToBeShown(item));
                    var imgStream = ClientApplication.class.getResourceAsStream("img/user_avatar_chat_icon.png");
                    if (imgStream == null)
                        throw new RuntimeException("image stream is null");
                    imageView.setImage(
                            new Image(imgStream));
                    setGraphic(imageView);
                }

            }
        };
    }

    public ListCell<Message> getMessageCellFactory() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else
                    setText(prepareMessageToBeShown(item));
            }
        };
    }
}
