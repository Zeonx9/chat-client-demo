package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.viewmodel.AllChatsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import lombok.Getter;

@Getter
public class AllChatsView extends AbstractView<AllChatsViewModel> {
    @FXML private Button createGroupButton;
    @FXML private ListView<Chat> chatListView;
    @Override
    public void init(AllChatsViewModel viewModel) {
        super.init(viewModel);

        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(chatListView -> viewModel.getChatListCellFactory());
        chatListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
        createGroupButton.setFocusTraversable(false);
    }

    @FXML protected void onNewChatClicked() {
        viewModel.showDialogAndWait();
    }
}
