package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.viewmodel.GroupCreationDialogModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Класс выступает в роли контроллера для диалогового окна для создания бемеды, управляет поведением и отображением элементов на экране
 */
@Getter
@Setter
public class GroupCreationDialog extends AbstractDialog<GroupRequest, GroupCreationDialogModel> {
    @FXML private TextField groupName;
    @FXML private ListView<User> listOfUsers;
    @FXML private ListView<User> selectedUsers;
    @FXML private Button createGroupButton;

    public static GroupCreationDialog getInstance(){
        return AbstractDialog.getInstance(GroupCreationDialog.class, "create-group-dialog-view.fxml", "ChatPageViewStyle");
    }

    /**
     * Метод вызывает функцию, которая заполняет ListView всеми пользователями компании
     * @param userList список всех пользователей компании, полученный из модели
     */
    public void populateUserList(List<User> userList) {
        viewModel.populateUserList(userList);
    }

    @Override
    protected void initialize() {
        groupName.textProperty().bindBidirectional(viewModel.getNameOfGroup());
        groupName.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onTextChanged));
        createGroupButton.disableProperty().bind(viewModel.getIsFilled());


        listOfUsers.itemsProperty().bind(viewModel.getUserListProperty());
        listOfUsers.setCellFactory(param -> viewModel.getUserListCellFactory());
        listOfUsers.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onNewMemberSelected));

        selectedUsers.itemsProperty().bind(viewModel.getSelectedUsersListProperty());
        selectedUsers.setCellFactory(param -> GroupCreationDialogModel.getSelectedUsersCellFactory());
        selectedUsers.setOnMouseClicked(viewModel::onMouseClickedListener);
        selectedUsers.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener((viewModel::onAlreadySelectedClickListener)));
    }

    public void setImageRequest(Function<String, CompletableFuture<Image>> imageRequest) {
        viewModel.setImageRequest(imageRequest);
    }

    @Override
    protected String getTitleString() {
        return "Create a group";
    }
}
