package com.ade.chatclient.view;

import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class AllUsersView {
    public Button showChats;
    public Button ShowUsers;
    @FXML public ListView<User> userListView;
    private AllUsersViewModel viewModel;
    public void init(AllUsersViewModel allUsersViewModel) {
        this.viewModel = allUsersViewModel;
        userListView.itemsProperty().bind(viewModel.getUsersListProperty());
        userListView.setCellFactory(param -> viewModel.getUserListCellFactory());
        userListView.getSelectionModel().selectedItemProperty().addListener(viewModel::onSelectedItemChange);
        viewModel.getAllUsers();
    }

    public void showAllChatsClicked(ActionEvent actionEvent) {
        viewModel.showChats();
    }

}
