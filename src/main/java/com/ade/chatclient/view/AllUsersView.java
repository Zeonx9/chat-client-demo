package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.AllUsersViewModel;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import com.ade.chatclient.viewmodel.LogInViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class AllUsersView {
    public Button showChats;
    public Button ShowUsers;
    private AllUsersViewModel viewModel;
    public void init(AllUsersViewModel allUsersViewModel) {
        // егор, разберись здесь прям построчно, что происходит
        this.viewModel = allUsersViewModel;

    }

    public void showAllChatsClicked(ActionEvent actionEvent) {
        viewModel.showChats();
    }

    public void showAllUsersClicked(ActionEvent actionEvent) {
    }
}
