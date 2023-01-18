package com.ade.chatclient.view;


import com.ade.chatclient.model.entities.Chat;
import com.ade.chatclient.model.entities.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChatPageView {
    @FXML private TextField searchTextField;

    @FXML private ListView<Chat> chatListView;
    @FXML private ListView<Message> messageListView;
    @FXML private TextField messageTextField;

    // disable button, if chat is not selected
    @FXML private Button sendButton;

    private ChatPageViewModel viewModel;

    public void init(ChatPageViewModel viewModel) {
        this.viewModel = viewModel;

        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(param -> viewModel.getChatListCellFactory());
        chatListView.getSelectionModel().selectedItemProperty().addListener(viewModel::onSelectedItemChange);
        viewModel.updateChatList();

        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        messageListView.setCellFactory(param -> viewModel.getMessageCellFactory());

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());
    }

    @FXML
    protected void onSendButtonClicked(ActionEvent actionEvent) {
        viewModel.sendMessage();
    }
}
