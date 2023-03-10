package com.ade.chatclient.view;

import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.application.ViewModelUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        userListView.setCellFactory(param -> viewModel.getUserListCellFactory());
        userListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
        userListView.getSelectionModel().clearSelection();
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // TODO Артем мне нужна твоя помощь туту
//        userListView.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
//            for (User user : viewModel.getUsersForNewChatProperty()) {
//                userListView.getSelectionModel().select(user);
//            }
//        });
    }

    public void OnCreateChatButtonClicked(ActionEvent actionEvent) {
        viewModel.createNewChat();
    }
}
