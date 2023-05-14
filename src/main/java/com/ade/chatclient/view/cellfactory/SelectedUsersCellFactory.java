package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * Фабрика ячеек выбранных пользователей для создания новой беседы, используется в диалоговом окне для создания группового чата, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
public class SelectedUsersCellFactory extends ListCell<User> {
    private final Label label = new Label();

    /**
     * Метод заполняет все значения в полях ячейки, а так же устанавливает label в качетсве графики, в котором написано имя пользователя
     * @param item объект класса User - пользователь
     * @param empty переменная типа boolean, показывает, является ли ячейка в списке пустой
     */
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

    /**
     *
     * @param user объект класса User - пользователь
     * @return ник пользователя в системе
     */
    private String prepareUserToBeShown(User user) {
        return user.getUsername();
    }
}
