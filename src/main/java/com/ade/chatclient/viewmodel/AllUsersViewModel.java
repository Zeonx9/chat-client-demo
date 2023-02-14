package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class AllUsersViewModel {
    private final ClientModel model;
    private final ViewHandler viewHandler;
    private final ListProperty<User> usersListProperty;
    private final StringProperty messageTextProperty;
    AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;
        messageTextProperty = new SimpleStringProperty();
        usersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    }


    public void getAllUsers() {
        usersListProperty.clear();
        usersListProperty.addAll(model.getAllUsers());
    }

    public void showChats() {
        try {
            viewHandler.openView(ViewHandler.Views.CHAT_PAGE_VIEW);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            messageTextProperty.set("cannot switch to another view!");
        }
    }
    private String prepareUserToBeShown(User user) {
        return user.getUsername();
    }

    public ListCell<User> getUserListCellFactory() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(prepareUserToBeShown(item));
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

    public void onSelectedItemChange(Observable observable, User oldValue, User newValue) {
        if (newValue == null) {
            System.out.println("somehow selected User is null");
            return;
        }
        model.createDialog(newValue);
        showChats();
    }

//    private void updateMessagesInSelectedChat() {
//        model.updateMessages();
//        messageListProperty.clear();
//        messageListProperty.addAll(model.getSelectedChatMessages());
//    }
}
