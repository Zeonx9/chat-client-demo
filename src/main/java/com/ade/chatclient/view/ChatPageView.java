package com.ade.chatclient.view;


import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.ChatPageViewModel;
import com.ade.chatclient.application.ViewModelUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

public class ChatPageView {
    @FXML private TextField searchChatsTextField;
    @FXML private TextField searchUsersTextField;
    @FXML private ListView<User> userListView;
    @FXML private BorderPane usersPane;
    @Getter
    @FXML private BorderPane chatsPane;
    @FXML private Button showChatsButton;
    @FXML private Button showUsersButton;

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
        chatListView.getSelectionModel().selectedItemProperty()
                .addListener(ViewModelUtils.changeListener(viewModel::onSelectedItemChange));

        messageListView.itemsProperty().bind(viewModel.getMessageListProperty());
        messageListView.setCellFactory(messageListView -> viewModel.getMessageCellFactory());
        messageListView.setFocusTraversable(false);
        viewModel.AddBottomScroller(messageListView);

        messageTextField.textProperty().bindBidirectional(viewModel.getMessageTextProperty());

        usersPane.setVisible(false);
        showChatsButton.setDisable(true);
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
        usersPane.setVisible(false);
        chatsPane.setVisible(true);
        showChatsButton.setDisable(true);
        showUsersButton.setDisable(false);
        showChatsButton.setFocusTraversable(false);
        showUsersButton.setFocusTraversable(false);
    }

    @FXML
    protected void onShowUsersClicked(ActionEvent actionEvent) {
        chatsPane.setVisible(false);
        usersPane.setVisible(true);
        showChatsButton.setDisable(false);
        showUsersButton.setDisable(true);
        showChatsButton.setFocusTraversable(false);
        showUsersButton.setFocusTraversable(false);
    }
}
