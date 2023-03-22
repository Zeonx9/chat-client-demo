package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
import static com.ade.chatclient.application.Views.ALL_CHATS_VIEW;

@Getter
public class AllUsersViewModel {
    private final ListProperty<User> usersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Setter
    private Pane placeHolder;
    private final ViewHandler viewHandler;
    private final ClientModel model;

    public AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;
        model.addListener("AllUsers", runLaterListener(listReplacer(usersListProperty)));
    }

    public void onSelectedItemChange(User newValue) {
        Chat created = model.createDialogFromAllUsers(newValue);
        model.setSelectedChat(created);
        model.getMessages();
        switchToChatPage();
    }

    private void switchToChatPage() {
        viewHandler.openPane(ALL_CHATS_VIEW, placeHolder);
    }

    private static String prepareUserToBeShown(User user) {
        return user.getUsername();
    }

    public static ListCell<User> getUserListCellFactory() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(prepareUserToBeShown(item));
                var imgStream = ClientApplication.class.getResourceAsStream("img/user_avatar_chat_icon.png");
                if (imgStream == null) {
                    throw new RuntimeException("image stream is null");
                }
                imageView.setImage(new Image(imgStream));
                setGraphic(imageView);
            }
        };
    }
}
