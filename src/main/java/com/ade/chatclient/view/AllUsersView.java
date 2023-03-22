package com.ade.chatclient.view;


import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

@Getter
public class AllUsersView {
    @FXML private Button createChatButton;
    @FXML private BorderPane usersPane;
    @FXML private TextField searchUsersTextField;
    @FXML private ListView<User> userListView;
    private AllUsersViewModel viewModel;

    public void init(AllUsersViewModel allUsersViewModel) {
        this.viewModel = allUsersViewModel;
        userListView.itemsProperty().bind(viewModel.getUsersListProperty());
        userListView.setCellFactory(param -> AllUsersViewModel.getUserListCellFactory());
        userListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.getSelectionModel().clearSelection();
    }

    public void OnCreateChatButtonClicked() {

    }
}
