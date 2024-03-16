package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Фабрика ячеек списка пользователей, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
public class UserListCellFactory extends ListCell<User> {
    @FXML private AnchorPane layout;
    @FXML private StackPane photoPane;
    @FXML private Label realNameLabel;
    @FXML private Label userNameLabel;

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

        Circle circle = new Circle(20, Color.rgb(145, 145, 145));
        Label label = new Label(prepareInitialsToBeShown(item));
        label.setStyle("-fx-text-fill: #FFFFFF");
        photoPane.getChildren().addAll(circle, label);

        setGraphic(layout);

    }

    private String prepareInitialsToBeShown(User user) {
        return user.getRealName().charAt(0) + "" + user.getSurname().charAt(0);
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
