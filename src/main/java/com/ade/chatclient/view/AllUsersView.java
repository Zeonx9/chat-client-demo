package com.ade.chatclient.view;

import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.viewmodel.ViewModelUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AllUsersView {
    public BorderPane usersPane;
    public TextField searchUsersTextField;
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;
    @FXML private ListView<User> userListView;
    private AllUsersViewModel viewModel;

    public void init(AllUsersViewModel allUsersViewModel) {
        this.viewModel = allUsersViewModel;
        userListView.itemsProperty().bind(viewModel.getUsersListProperty());
        userListView.setCellFactory(param -> viewModel.getUserListCellFactory());
        userListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
        userListView.getSelectionModel().clearSelection();
    }

    public void onShowChatsClicked(ActionEvent actionEvent) {
        viewModel.switchToChatPage();
    }

}
