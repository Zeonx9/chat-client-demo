package com.ade.chatclient.view;


import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;

/**
 * Класс выступает в роли контроллера для основного окна приложения, управляет поведением и отображением элементов на экране
 */
public class ChatPageView extends AbstractView<ChatPageViewModel> {
    @FXML private AnchorPane messageField;
    @FXML private Button showUserProfileButton;
    @FXML private Button createGroupButton;
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;
    @FXML private Label chatNameLabel;
    @FXML private Label chatInfoLabel;
    @FXML private Label viewNameLabel;
    @FXML private ListView<Message> messageListView;
    @FXML private TextField messageTextField;
    @FXML private Button infoButton;
    @FXML private Button sendButton;
    @FXML private StackPane photoPane;
    @FXML private AnchorPane infoArea;
    @Getter
    @FXML private Pane placeHolder;

    @Override
    protected void initialize() {
        viewModel.addBottomScroller(messageListView);
        viewModel.addPaneSwitcher(placeHolder);

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());
        chatNameLabel.textProperty().bind(viewModel.getSelectedChatNameProperty());
        chatInfoLabel.textProperty().bind(viewModel.getSelectedChatInfoProperty());
        viewNameLabel.textProperty().bind(viewModel.getOpenViewNameProperty());
        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        showChatsButton.disableProperty().bind(viewModel.getShowChatsButtonDisabled());
        showUsersButton.disableProperty().bind(viewModel.getShowUsersButtonDisabled());
        showUserProfileButton.disableProperty().bind(viewModel.getShowUserProfileDisabled());

        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());
        messageTextField.setOnKeyPressed(ViewModelUtils.enterKeyHandler(viewModel::sendMessage));

        infoButton.opacityProperty().bind(viewModel.getOpacityProperty());
        infoButton.disableProperty().bind(viewModel.getDisableProperty());
        createGroupButton.setFocusTraversable(false);

        messageField.opacityProperty().bind(viewModel.getOpacityProperty());
        messageTextField.disableProperty().bind(viewModel.getDisableProperty());
        sendButton.opacityProperty().bind(viewModel.getOpacityProperty());
        sendButton.disableProperty().bind(viewModel.getDisableProperty());

        infoArea.opacityProperty().bind(viewModel.getInfoAreaOpacityProperty());
        infoArea.disableProperty().bind(viewModel.getInfoAreaDisableProperty());

        Bindings.bindContentBidirectional(photoPane.getChildren(), viewModel.getChatIconNodes());

        viewModel.openChatPane();
    }

    /**
     * Метод отображает на экране отправленное пользователем сообщение и создает запрос на его отправку, срабатывает при нажатии на кнопку со значком отправки сообщения
     */
    @FXML
    protected void onSendButtonClicked() {
        viewModel.sendMessage();
    }

    @FXML
    protected void onUploadPhotoButtonClicked() {
        return;
    }

    /**
     * Метод вызывает функцию смены Pane внутри placeHolder на окно со всеми чатами пользователя
     */
    @FXML
    protected void onShowChatsClicked() {
        viewModel.openChatPane();
    }

    /**
     * Метод вызывает функцию смены Pane внутри placeHolder на окно со всеми пользователями в компании
     */
    @FXML
    protected void onShowUsersClicked() {
        viewModel.openUsersPane();
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для создания нового чата, после чего вызывает функцию создания беседы в Model, срабатывает при нажатии на кнопку Create Group
     */
    @FXML protected void onNewChatClicked() {
        viewModel.showDialogAndWait();
    }

    /**
     * Метод вызывает функцию смены Pane внутри placeHolder на окно с личным кабинетом пользователя и функциями его управления
     */
    @FXML protected void onShowUserProfileClicked() {
        viewModel.openProfilePane();
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для просмотра информации о беседе, в которой пользователь сейчас находится
     */
    @FXML protected void onInfoButtonClicked() {
        viewModel.showDialog();
    }
}
