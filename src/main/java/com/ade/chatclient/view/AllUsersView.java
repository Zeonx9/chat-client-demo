package com.ade.chatclient.view;


import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Getter;

/**
 * Класс выступает в роли контроллера для панели с пользователями, управляет поведением и отображением элементов на экране
 */
@Getter
public class AllUsersView extends AbstractView<AllUsersViewModel> {
    @FXML private TextField searchText;
    @FXML private ListView<User> userListView;

    @Override
    protected void initialize() {
        searchText.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onTextChanged));
        userListView.itemsProperty().bind(viewModel.getUsersListProperty());
        userListView.setCellFactory(userListView -> viewModel.getUserListCellFactory());
        userListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
    }
}
