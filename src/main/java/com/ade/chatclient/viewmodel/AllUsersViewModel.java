package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllUsersViewModel {
    private final ClientModel model;
    private final ViewHandler viewHandler;
    private final StringProperty messageTextProperty;
    AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;
        messageTextProperty = new SimpleStringProperty();

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
    private String prepareUserToBeShown(Chat chat) {
//        List<String> memberNames = new ArrayList<>();
//        chat.getMembers().forEach(member -> {
//            if (!Objects.equals(member.getId(), model.getMyself().getId()))
//                memberNames.add(member.getName());
//        });
//        return String.join(", ", memberNames);
    }
    public ListCell<Chat> getUserListCellFactory() {
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
}
