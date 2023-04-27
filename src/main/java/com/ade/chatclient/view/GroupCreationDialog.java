package com.ade.chatclient.view;

import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.viewmodel.GroupCreationDialogModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
@Getter

public class GroupCreationDialog extends Dialog<GroupRequest> {
    @FXML private TextField groupName;
    @FXML private ListView<User> listOfUsers;
    @FXML private ListView<User> selectedUsers;
    @FXML private Button createGroupButton;
    @FXML private DialogPane createGroupDialog;
    private GroupCreationDialogModel viewModel;

    public void init(List<User> userList, GroupCreationDialogModel viewModel) {
        this.viewModel = viewModel;
        setTitle("Creation group");
        setResultConverter(viewModel::resultConverter);

        groupName.textProperty().bindBidirectional(viewModel.getNameOfGroup());
        groupName.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onTextChanged));
        createGroupButton.disableProperty().bind(viewModel.getIsFilled());


        listOfUsers.itemsProperty().bind(viewModel.getUserListProperty());
        listOfUsers.setCellFactory(param -> AllUsersViewModel.getUserListCellFactory());
        listOfUsers.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onNewMemberSelected));

        selectedUsers.itemsProperty().bind(viewModel.getSelectedUsersListProperty());
        selectedUsers.setCellFactory(param -> GroupCreationDialogModel.getSelectedUsersCellFactory());
        selectedUsers.setOnMouseClicked(viewModel::onMouseClickedListener);
        selectedUsers.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener((viewModel::onAlreadySelectedClickListener)));

        viewModel.populateUserList(userList);

    }

    public static GroupCreationDialog getInstance(){
        FXMLLoader loader = new FXMLLoader(GroupCreationDialog.class.getResource("create-group-dialog-view.fxml"));
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GroupCreationDialog controller = loader.getController();
        controller.setDialogPane(pane);
        return controller;
    }

    public void OnCreateGroupButtonClicked() {
        setResult(viewModel.onCreateClicked());
    }
}
