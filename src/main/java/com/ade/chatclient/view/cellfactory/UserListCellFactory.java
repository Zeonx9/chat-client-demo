package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.User;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserListCellFactory extends ListCell<User> {

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

    private static String prepareUserToBeShown(User user) {
        return user.getUsername();
    }
}
