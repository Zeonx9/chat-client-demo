package com.ade.chatclient.view;


import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChatPageView {
    @FXML private Button createChatButton;
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;
    @FXML private TextField searchTextField;

    @FXML private ListView<Chat> chatListView;
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

        chatListView.itemsProperty().bind(viewModel.getChatListProperty());
        chatListView.setCellFactory(chatListView -> viewModel.getChatListCellFactory());
        chatListView.getSelectionModel().selectedItemProperty().addListener(viewModel::onSelectedItemChange);
        // удаление первого выделения не работает туи, думаю из-за потоков, так как на AllUsers она работает корректно
        chatListView.getSelectionModel().clearSelection();

        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());
        messageListView.setFocusTraversable(false);

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());
    }

    @FXML
    protected void onSendButtonClicked(ActionEvent actionEvent) {
        viewModel.sendMessage();
    }

    @FXML
    protected void onCreateChatClicked(ActionEvent actionEvent) {
    }

    @FXML
    protected void onShowChatsClicked(ActionEvent actionEvent) {
    }

    @FXML
    protected void onShowUsersClicked(ActionEvent actionEvent) {
        viewModel.showUsers();
    }
}
