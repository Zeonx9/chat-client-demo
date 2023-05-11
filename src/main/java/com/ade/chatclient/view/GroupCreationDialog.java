package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractDialog;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.viewmodel.GroupCreationDialogModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GroupCreationDialog extends AbstractDialog<GroupRequest, GroupCreationDialogModel> {
    @FXML private TextField groupName;
    @FXML private ListView<User> listOfUsers;
    @FXML private ListView<User> selectedUsers;
    @FXML private Button createGroupButton;

    @Override
    public void init(GroupCreationDialogModel viewModel) {
        super.init(viewModel);
        setTitle("Creation group");

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
    }

    public static GroupCreationDialog getInstance(){
        return AbstractDialog.getInstance(GroupCreationDialog.class, "create-group-dialog-view.fxml");
    }

    public void populateUserList(List<User> userList) {
        viewModel.populateUserList(userList);
    }
}
