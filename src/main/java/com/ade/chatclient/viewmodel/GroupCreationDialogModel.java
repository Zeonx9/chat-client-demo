package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractDialogModel;
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

import java.util.List;

@Getter
public class GroupCreationDialogModel extends AbstractDialogModel<GroupRequest> {
    private final ListProperty<User> userListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<User> selectedUsersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty nameOfGroup = new SimpleStringProperty();
    private final BooleanProperty isFilled = new SimpleBooleanProperty(true);
    private User selected;

    public void populateUserList(List<User> userList) {
        userListProperty.setAll(userList);
    }

    public void onNewMemberSelected(User user) {
        if (user == null || selectedUsersListProperty.contains(user)) {
            return;
        }
        selectedUsersListProperty.add(user);
    }

    public void onAlreadySelectedClickListener(User user) {
        if (user != null) {
            selected = user;
            System.out.println(selected);
        }
    }

    public void onTextChanged(String newText) {
        if (newText == null) {
            newText = "";
        }
        isFilled.set(newText.isBlank());
    }

    public void onMouseClickedListener(MouseEvent evt) {
        if (selected != null) {
            selectedUsersListProperty.remove(selected);
            System.out.println(selectedUsersListProperty);
        }
    }

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

    public static ListCell<User> getSelectedUsersCellFactory() {
        return new SelectedUsersCellFactory();
    }

    @Override
    public GroupRequest onOkClicked() {
        return resultConverter(ButtonType.OK);
    }
}
