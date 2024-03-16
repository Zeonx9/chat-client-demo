package com.ade.chatclient.application.structure;

import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;

/**
 * класс, который предназначе заменять вью-модел в диалгах, где она на требуется
 */
public final class EmptyDialogModel<T> extends AbstractDialogModel<T> {
    @Override
    public T resultConverter(ButtonType buttonType) {
        return null;
    }

    public ListCell<User> getUserListCellFactory() {
        return ViewModelUtils.loadCellFactory(
                UserListCellFactory.class,
                "user-list-cell-factory.fxml"
        );
    }
}
