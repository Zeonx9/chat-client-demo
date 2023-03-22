package com.ade.chatclient.view;


import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Getter;

@Getter
public class AllUsersView extends AbstractView<AllUsersViewModel> {
    @FXML private TextField searchUsersTextField;
    @FXML private ListView<User> userListView;

    @Override
    public void init(AllUsersViewModel allUsersViewModel) {
        super.init(allUsersViewModel);

        userListView.itemsProperty().bind(viewModel.getUsersListProperty());
        userListView.setCellFactory(param -> AllUsersViewModel.getUserListCellFactory());
        userListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
    }
}
