package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import static com.ade.chatclient.viewmodel.ViewModelUtils.listReplacer;
import static com.ade.chatclient.viewmodel.ViewModelUtils.runLaterListener;

@Getter
public class AllUsersViewModel {
    private final ClientModel model;
    private final ViewHandler viewHandler;
    private final ListProperty<User> usersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;

        model.addListener("AllUsers", runLaterListener(listReplacer(usersListProperty)));
    }

    public void switchToChatPage() {
        viewHandler.openView(ViewHandler.Views.CHAT_PAGE_VIEW);
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

    public void onSelectedItemChange(User newValue) {
        if (newValue == null) {
            System.out.println("Selected User is null");
            return;
        }
        Chat created = model.createDialogFromAllUsers(newValue);
        model.setSelectedChat(created);
        model.getMessages();
        switchToChatPage();
    }
}
