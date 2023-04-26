package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.application.Views;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.cellfactory.MessageListCellFactory;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.function.Consumer;

import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;

@Getter
public class ChatPageViewModel extends AbstractViewModel<ClientModel> {
    private final ListProperty<Message> messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private final BooleanProperty showChatsButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty showUsersButtonDisabled = new SimpleBooleanProperty(false);
    private final StringProperty userNameProperty = new SimpleStringProperty();
    private Runnable bottomScroller;
    private Consumer<Views> paneSwitcher;

    public ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener("gotMessages", runLaterListener(this::fillMessageHistory));
        model.addListener("newMessagesInSelected", runLaterListener(this::newSelectedMessages));
    }

    private void fillMessageHistory(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) event.getNewValue();
        messageListProperty.clear();
        messageListProperty.addAll(messages);
        userNameProperty.setValue(model.getSelectedChat().getChatName());
        bottomScroller.run();
    }

    private void newSelectedMessages(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        List<Message> selectedChatMessages = (List<Message>) event.getNewValue();
        messageListProperty.addAll(selectedChatMessages);
        bottomScroller.run();
    }

    public <T> void addBottomScroller(ListView<T> listView) {
        bottomScroller = () -> {
            if (!messageListProperty.isEmpty()) {
                listView.scrollTo(messageListProperty.size() - 1);
            }
        };
    }

    public void addPaneSwitcher(Pane placeHolder) {
        paneSwitcher = viewType -> viewHandler.openPane(viewType, placeHolder);
    }

    public void openChatPane() {
        paneSwitcher.accept(Views.ALL_CHATS_VIEW);
    }
    public void openUsersPane() {
        paneSwitcher.accept(Views.ALL_USERS_VIEW);
    }

    public void sendMessage() {
        if (messageTextProperty.get().isBlank()) {
            return;
        }
        model.sendMessageToChat(messageTextProperty.get());
        messageTextProperty.set("");
    }

    public void changeButtonsParam(Boolean param) {
        showChatsButtonDisabled.set(param);
        showUsersButtonDisabled.set(!param);
    }

    public ListCell<Message> getMessageCellFactory() {
        MessageListCellFactory factory = ViewModelUtils.loadCellFactory(
                MessageListCellFactory.class,
                "message-list-cell-factory.fxml"
        );
        factory.init(model.getMyself().getId());
        return factory;
    }
}
