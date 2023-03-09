package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.AllChatsViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import javafx.fxml.FXML;
import com.ade.chatclient.domain.Chat;

@Getter
public class AllChatsView {

    @FXML public TextField searchChatsTextField;
    @FXML public BorderPane chatsPane;
    @FXML public ListView<Chat> chatListView;

    public void init(AllChatsViewModel viewModel) {
        this.viewModel = viewModel;

        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(chatListView -> viewModel.getChatListCellFactory());
        chatListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));


    }
}
