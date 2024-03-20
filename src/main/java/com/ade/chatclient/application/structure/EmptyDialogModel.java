package com.ade.chatclient.application.structure;

import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * класс, который предназначе заменять вью-модел в диалгах, где она на требуется
 */
public final class EmptyDialogModel<T> extends AbstractDialogModel<T> {
    @Setter
    @Getter
    private Function<String, CompletableFuture<Image>> imageRequest;

    @Override
    public T resultConverter(ButtonType buttonType) {
        return null;
    }

    public ListCell<User> getUserListCellFactory() {
        UserListCellFactory factory = ViewModelUtils.loadCellFactory(
                UserListCellFactory.class,
                "user-list-cell-factory.fxml"
        );
        factory.init(imageRequest);
        return factory;
    }
}
