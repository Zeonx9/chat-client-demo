package com.ade.chatclient.view;


import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.Getter;

public class ChatPageView {
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;

    @FXML private ListView<Message> messageListView;
    @FXML private TextField messageTextField;
    @FXML private Button sendButton;
    @Getter
    public Pane switchPane;
    private ChatPageViewModel viewModel;

    /**
     * метод, который выполняет инициализацию вместо конструктора,
     * так как объекты View получаются при подключении не через конструктор, а из FXMLLoader
     * выполняет binding модели и вью модели
     * @param viewModel ссылка на вью-модель, которая управляет этим вью
     */
    public void init(ChatPageViewModel viewModel) {
        this.viewModel = viewModel;

        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());
        messageListView.setFocusTraversable(false);

        viewModel.AddBottomScroller(messageListView);
        viewModel.setSwitchPane(switchPane);

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());

        showChatsButton.disableProperty().bind(viewModel.getShowChatsButtonDisabled());
        showChatsButton.focusTraversableProperty().bind(viewModel.getButtonFocused());
        showUsersButton.disableProperty().bind(viewModel.getShowUsersButtonDisabled());
        showUsersButton.focusTraversableProperty().bind(viewModel.getButtonFocused());
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
}
