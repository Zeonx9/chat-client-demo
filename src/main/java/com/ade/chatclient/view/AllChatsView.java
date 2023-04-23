package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.viewmodel.AllChatsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import lombok.Getter;

import java.util.Optional;

@Getter
public class AllChatsView extends AbstractView<AllChatsViewModel> {
    @FXML private ListView<Chat> chatListView;
    @Override
    public void init(AllChatsViewModel viewModel) {
        super.init(viewModel);

        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(chatListView -> viewModel.getChatListCellFactory());
        chatListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));
    }

    @FXML protected void onNewChatClicked() {
        Dialog<String> dialog = new TextInputDialog("Тут текст");
        dialog.setTitle("Ну типа диалоговое окно");
        Optional<String> answer = dialog.showAndWait();
        System.out.println(answer.orElse("текст не ввели("));
    }
}
