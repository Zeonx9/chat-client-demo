package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.viewmodel.AllChatsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Getter;

/**
 * Класс выступает в роли контроллера для панели с чатами, управляет поведением и отображением элементов на экране
 */
@Getter
public class AllChatsView extends AbstractView<AllChatsViewModel> {
    @FXML private TextField searchText;
    @FXML private Button createGroupButton;
    @FXML private ListView<Chat> chatListView;

    @Override
    protected void initialize() {
        searchText.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onTextChanged));
        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(chatListView -> viewModel.getChatListCellFactory());
        chatListView.setOnMouseClicked(viewModel::onMouseClickedListener);

        viewModel.addSelector(chatListView);

        createGroupButton.setFocusTraversable(false);
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для создания нового чата, после чего вызывает функцию создания беседы в Model, срабатывает при нажатии на кнопку Create Group
     */
    @FXML protected void onNewChatClicked() {
        viewModel.showDialogAndWait();
    }
}
