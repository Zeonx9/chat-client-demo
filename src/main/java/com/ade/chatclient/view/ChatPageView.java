package com.ade.chatclient.view;


import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChatPageView {
    // TODO Егор не должно быть public полей и методов
    // TODO Егор переименовать методы кнопок нормально
    public Button createChatBtn;
    public Button showChats;
    public Button ShowUsers;
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

        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());
    }

    @FXML
    protected void onSendButtonClicked(ActionEvent actionEvent) {
        viewModel.sendMessage();
    }

    public void showAllUsersClicked(ActionEvent actionEvent) {
        viewModel.showUsers();
    }

    public void createNewChatClicked(ActionEvent actionEvent) {
    }

    public void showAllChatsClicked(ActionEvent actionEvent) {

    }
}
