package com.ade.chatclient.view;


import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.Getter;

public class ChatPageView extends AbstractView<ChatPageViewModel> {
    @FXML private Button showUserProfileButton;
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;
    @FXML private Label userNameLabel;
    @FXML private ListView<Message> messageListView;
    @FXML private TextField messageTextField;
    @FXML private Button infoButton;
    @Getter
    @FXML private Pane placeHolder;

    @Override
    public void init(ChatPageViewModel viewModel) {
        super.init(viewModel);

        viewModel.addBottomScroller(messageListView);
        viewModel.addPaneSwitcher(placeHolder);

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());
        userNameLabel.textProperty().bind(viewModel.getSelectedChatNameProperty());
        showChatsButton.disableProperty().bind(viewModel.getShowChatsButtonDisabled());
        showUsersButton.disableProperty().bind(viewModel.getShowUsersButtonDisabled());
        showUserProfileButton.disableProperty().bind(viewModel.getShowUserProfileDisabled());
        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());

        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());
        messageTextField.setOnKeyPressed(ViewModelUtils.enterKeyHandler(viewModel::sendMessage));

        infoButton.opacityProperty().bind(viewModel.getOpacityProperty());
        infoButton.focusTraversableProperty().bind(viewModel.getInfoButtonFocusProperty());

        viewModel.openChatPane();
    }

    @FXML
    protected void onSendButtonClicked() {
        viewModel.sendMessage();
    }

    @FXML
    protected void onShowChatsClicked() {
        viewModel.openChatPane();
    }

    @FXML
    protected void onShowUsersClicked() {
        viewModel.openUsersPane();
    }

    public void onShowUserProfileClicked() {
        viewModel.openProfilePane();
    }

    public void onInfoButtonClicked() {
        viewModel.showDialog();
    }
}
