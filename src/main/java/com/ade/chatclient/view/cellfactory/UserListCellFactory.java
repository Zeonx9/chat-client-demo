package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.User;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Фабрика ячеек списка пользователей, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
public class UserListCellFactory extends ListCell<User> {

    private final ImageView imageView = new ImageView();

    /**
     * Метод заполняет все значения в полях ячейки, а так же устанавливает imageView в качестве графики - иконка пользователя
     * @param item объект класса User - пользователь
     * @param empty переменная типа boolean, показывает, является ли ячейка в списке пустой
     */
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

    /**
     *
     * @param user объект класса User - пользователь
     * @return имя и фамилию пользователя
     */
    private static String prepareUserToBeShown(User user) {
        return user.getRealName() + " " + user.getSurname();
    }
}
