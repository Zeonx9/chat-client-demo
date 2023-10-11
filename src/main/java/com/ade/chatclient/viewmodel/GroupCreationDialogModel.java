package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.view.cellfactory.SelectedUsersCellFactory;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс отвечающий за окно для создания группового чата, связан с GroupCreationDialog(view)
 */
@Getter
@Setter
public class GroupCreationDialogModel extends AbstractDialogModel<GroupRequest> {
    private final ListProperty<User> userListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<User> selectedUsersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty nameOfGroup = new SimpleStringProperty();
    private final BooleanProperty isFilled = new SimpleBooleanProperty(true);
    private User selected;

    /**
     * Заполняет ListView всеми пользователями в компании
     * @param userList список со всеми пользователями
     */
    public void populateUserList(List<User> userList) {
        userListProperty.setAll(userList);
    }

    /**
     * Метод срабатывает при нажатии на пользователя для добавления его в беседу и добавляет его в список к уже выбранным пользователям
     * @param user выбранный пользователь
     */
    public void onNewMemberSelected(User user) {
        if (user == null || selectedUsersListProperty.contains(user)) {
            return;
        }
        selectedUsersListProperty.add(user);
    }

    /**
     * Метод записывает выбранного пользователя в переменную для дальнейшего удаления
     * @param user выбранный пользователь, который должен быть удален из списка участников чата
     */
    public void onAlreadySelectedClickListener(User user) {
        if (user != null) {
            selected = user;
            System.out.println(selected);
        }
    }

    /**
     * Метод, который проверяет, заполнено ли поле с названием группы
     * @param newText информация, введенная пользователем в TextField
     */
    public void onTextChanged(String newText) {
        if (newText == null) {
            newText = "";
        }
        isFilled.set(newText.isBlank());
    }

    /**
     * Метод срабатывает при нажатии на выбранного пользователя для его удаления из списка пользователей в будущей беседе
     */
    public void onMouseClickedListener(MouseEvent evt) {
        if (selected != null) {
            selectedUsersListProperty.remove(selected);
            System.out.println(selectedUsersListProperty);
        }
    }

    /**
     * Метод собирает введенные пользователем данные и формирует GroupRequest для создания новой беседы
     * @param buttonType как именно было закрыто диалоговое окно (ОК или CLOSE)
     * @return GroupRequest - данные для запроса на создание нового группового чата
     */
    @Override
    public GroupRequest resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }
        GroupChatInfo info = GroupChatInfo.builder().name(nameOfGroup.getValue()).build();
        GroupRequest request = GroupRequest.builder().groupInfo(info).build();
        request.getIds().addAll(selectedUsersListProperty.stream().map(User::getId).toList());
        return request;
    }

    /**
     * @return фабрику ячеек для листа пользователей, которые выбраны участниками будущего чата
     */
    public static ListCell<User> getSelectedUsersCellFactory() {
        return new SelectedUsersCellFactory();
    }
}
