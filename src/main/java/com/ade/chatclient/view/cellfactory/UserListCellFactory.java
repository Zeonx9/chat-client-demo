package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Фабрика ячеек списка пользователей, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
public class UserListCellFactory extends ListCell<User> {
    @FXML private AnchorPane layout;
    @FXML private StackPane photoPane;
    @FXML private Label realNameLabel;
    @FXML private Label userNameLabel;
    private Function<String, CompletableFuture<Image>> imageRequest;

    public void init(Function<String, CompletableFuture<Image>> imageRequest) {
        this.imageRequest = imageRequest;
    }

    /**
     * Метод заполняет все значения в полях ячейки, а так же устанавливает imageView в качестве графики - иконка пользователя
     * @param item объект класса User - пользователь
     * @param empty переменная типа boolean, показывает, является ли ячейка в списке пустой
     */
    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        realNameLabel.setText(prepareUserToBeShown(item));
        userNameLabel.setText(item.getUsername());

        UserPhoto.setPaneContent(photoPane.getChildren(), item, 20, imageRequest);

        setGraphic(layout);

    }


    /**
     *
     * @param user объект класса User - пользователь
     * @return имя и фамилию пользователя
     */
    private static String prepareUserToBeShown(User user) {
        return user.getRealName() + " " + user.getSurname();
    }


}
