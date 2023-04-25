package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class SelectedUsersCellFactory extends ListCell<User> {
    private final Label label = new Label();

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        label.setText(prepareUserToBeShown(item));
        setGraphic(label);
    }

    private String prepareUserToBeShown(User user) {
        return user.getUsername();
    }
}
