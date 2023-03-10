package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.AllChatsViewModel;
import com.ade.chatclient.application.ViewModelUtils;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import javafx.fxml.FXML;
import com.ade.chatclient.domain.Chat;

@Getter
public class AllChatsView {

    @FXML private TextField searchChatsTextField;
    @FXML private BorderPane chatsPane;
    @FXML private ListView<Chat> chatListView;
    private AllChatsViewModel viewModel;
    public void init(AllChatsViewModel viewModel) {
        this.viewModel = viewModel;

        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(chatListView -> viewModel.getChatListCellFactory());
        chatListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
    }
}
