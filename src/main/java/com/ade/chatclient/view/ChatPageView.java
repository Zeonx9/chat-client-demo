package com.ade.chatclient.view;


import com.ade.chatclient.application.Views;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class ChatPageView {
    public Pane switchPane;
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;

    @FXML private ListView<Message> messageListView;
    @FXML private TextField messageTextField;

    @FXML private Button sendButton;

    private ChatPageViewModel viewModel;

    /**
     * метод, который выполняет инициализацию вместо конструкора,
     * так как объекты вью получаются при подключении не через констуктор, а из FXMLLoader
     * выполняет байндинг модели и вью модели
     * @param viewModel ссылка на вью-модель, которая управляет этим вью
     */
    public void init(ChatPageViewModel viewModel) {
        this.viewModel = viewModel;

        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());
        messageListView.setFocusTraversable(false);
        viewModel.AddBottomScroller(messageListView);

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());

        showChatsButton.disableProperty().bind(viewModel.getShowChatsButtonDisabled());
        showChatsButton.focusTraversableProperty().bind(viewModel.getButtonFocused());
        showUsersButton.disableProperty().bind(viewModel.getShowUsersButtonDisabled());
        showUsersButton.focusTraversableProperty().bind(viewModel.getButtonFocused());

    }

    @FXML
    protected void onSendButtonClicked(ActionEvent actionEvent) {
        viewModel.sendMessage();
    }

    @FXML
    protected void onShowChatsClicked(ActionEvent actionEvent) {
        viewModel.ChangePane(Views.ALL_CHATS_VIEW, switchPane);
        viewModel.changeButtonsParam(true);
    }

    @FXML
    protected void onShowUsersClicked(ActionEvent actionEvent) {
        viewModel.ChangePane(Views.ALL_USERS_VIEW, switchPane);
        viewModel.changeButtonsParam(false);
    }

    public Pane getChatsPane() {
        return switchPane;
    }
}
