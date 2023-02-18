package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ChatPageViewModel {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Message> messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private final ViewHandler viewHandler;
    private final ClientModel model;

    ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        this.viewHandler = viewHandler;
        this.model = model;

        // методы, которые запустит модель во время изменений
        model.addListener("MyChatsUpdate", this::updateMyChats);
        model.addListener("MessageUpdate", this::updateMessage);
    }

    private void updateMyChats(PropertyChangeEvent propertyChangeEvent) {
        Platform.runLater(() -> {
            List<Chat> myChats = (List<Chat>) propertyChangeEvent.getNewValue();
            chatListProperty.clear();
            chatListProperty.addAll(myChats);
        });
    }

    private void updateMessage(PropertyChangeEvent propertyChangeEvent) {
        Platform.runLater(() -> {
            List<Message> selectedChatMessages = (List<Message>) propertyChangeEvent.getNewValue();
            messageListProperty.clear();
            messageListProperty.addAll(selectedChatMessages);
        });
    }

    public void onSelectedItemChange(Observable observable, Chat oldValue, Chat newValue) {
        if (newValue == null) {
            return;
        }
        model.setSelectedChat(newValue);
        model.getMessages();
    }

    public void sendMessage() {
        if (messageTextProperty.get().isBlank()) {
            return;
        }
        model.sendMessageToChat(messageTextProperty.get());
        messageTextProperty.set("");
    }

    public void showUsers() {
        // TODO Егор назови метод нормально, и разберись с полями
        try {
            viewHandler.openView(ViewHandler.Views.ALL_USERS_VIEW);
        }
        catch (IOException e) {
            System.out.println("cannot switch to all users view " + e.getMessage());
        }
    }

    private String prepareChatToBeShown(Chat chat) {
        List<String> memberNames = new ArrayList<>();
        chat.getMembers().forEach(member -> {
            if (!Objects.equals(member.getId(), model.getMyself().getId()))
                memberNames.add(member.getUsername());
        });
        return String.join(", ", memberNames);
    }

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return "from " + msg.getAuthor().getUsername() +
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
                    return;
                }
                setText(prepareChatToBeShown(item));
                var imgStream = ClientApplication.class.getResourceAsStream("img/user_avatar_chat_icon.png");
                if (imgStream == null) {
                    throw new RuntimeException("image stream is null");
                }
                imageView.setImage(new Image(imgStream));
                setGraphic(imageView);
            }
        };
    }

    public ListCell<Message> getMessageCellFactory() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(prepareMessageToBeShown(item));
                }
            }
        };
    }
}
