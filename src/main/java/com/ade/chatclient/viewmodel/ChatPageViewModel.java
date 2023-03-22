package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.Views;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.application.ViewHandler;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;

@Getter
public class ChatPageViewModel {
    private final ListProperty<Message> messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private Runnable bottomScroller = () -> {};
    private final BooleanProperty showChatsButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty showUsersButtonDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty buttonFocused = new SimpleBooleanProperty(false);
    private final StringProperty userNameProperty = new SimpleStringProperty();
    private final ViewHandler viewHandler;
    private final ClientModel model;
    public ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        this.viewHandler = viewHandler;
        this.model = model;

        // методы, которые запустит модель во время изменений
        model.addListener("MessageUpdate", runLaterListener(this::updateMessages));
        model.addListener("newSelectedMessages", runLaterListener(this::newSelectedMessages));

        // надо новый слушатель для incoming messages, который просто добавляет их в конец
    }
    private void updateMessages(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) event.getNewValue();
        messageListProperty.clear();
        messageListProperty.addAll(messages);
        userNameProperty.setValue(model.getSelectedChat().membersAsString());
        bottomScroller.run();
    }

    private void newSelectedMessages(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        List<Message> selectedChatMessages = (List<Message>) event.getNewValue();
        messageListProperty.addAll(selectedChatMessages);
        bottomScroller.run();
    }

    public void sendMessage() {
        if (messageTextProperty.get().isBlank()) {
            return;
        }
        model.sendMessageToChat(messageTextProperty.get());
        messageTextProperty.set("");
    }

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return "from " + msg.getAuthor().getUsername() +
                ": " + msg.getText() +
                " at " + msg.getDateTime().format(dtf);
    }

    public ListCell<Message> getMessageCellFactory() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(prepareMessageToBeShown(item));
                }
            }
        };
    }
    public <T> void AddBottomScroller(ListView<T> listView) {
        bottomScroller = () -> {
            if (!messageListProperty.isEmpty()) {
                listView.scrollTo(messageListProperty.size() - 1);
            }
        };
    }
    public void ChangePane(Views paneType, Pane pane){
        viewHandler.openPane(paneType, pane);
    }
    public void changeButtonsParam(Boolean param) {
        showChatsButtonDisabled.set(param);
        showUsersButtonDisabled.set(!param);
        buttonFocused.set(false);
    }

    /* этот метод сейчас не нужен, но может потом пригодится

    public void switchToAllUsers() {
        viewHandler.openView(ViewHandler.Views.ALL_USERS_VIEW);
    }
*/
}
