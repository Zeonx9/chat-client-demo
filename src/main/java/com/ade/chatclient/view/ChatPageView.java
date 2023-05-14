package com.ade.chatclient.view;


import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.Getter;

/**
 * Класс выступает в роли контроллера для основного окна приложения, управляет поведением и отображением элементов на экране
 */
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
    protected void initialize() {
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

    /**
     * Метод отображает на экране отправленное пользователем сообщение и создает запрос на его отправку, срабатывает при нажатии на кнопку со значком отправки сообщения
     */
    @FXML
    protected void onSendButtonClicked() {
        viewModel.sendMessage();
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
     * Метод вызывает функцию смены Pane внутри placeHolder на окно с личным кабинетом пользователя и функциями его управления
     */
    public void onShowUserProfileClicked() {
        viewModel.openProfilePane();
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для просмотра информации о беседе, в которой пользователь сейчас находится
     */
    public void onInfoButtonClicked() {
        viewModel.showDialog();
    }
}
