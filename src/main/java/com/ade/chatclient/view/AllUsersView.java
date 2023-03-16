package com.ade.chatclient.view;


import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.application.ViewModelUtils;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

import javax.swing.event.ChangeListener;

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
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.getSelectionModel().clearSelection();
        // TODO Артем мне нужна твоя помощь туту

//        userListView.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
//            for (User user : viewModel.getUsersForNewChat()) {
//                userListView.getSelectionModel().;
//            }
//        });
    }

    public void OnCreateChatButtonClicked(ActionEvent actionEvent) {
//        viewModel.createNewChat();
    }
}
