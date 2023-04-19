package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractViewModel;
import com.ade.chatclient.application.Views;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.application.ViewHandler;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.time.format.DateTimeFormatter;
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

        model.addListener("MessageUpdate", runLaterListener(this::updateMessages));
        model.addListener("newSelectedMessages", runLaterListener(this::newSelectedMessages));
    }

    private void updateMessages(PropertyChangeEvent event) {
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

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return msg.getText() + " at " + msg.getDateTime().format(dtf);
    }

    public ListCell<Message> getMessageCellFactory() {
        return new ListCell<>() {
            private final Label label = new Label();
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (item.getAuthor().getId().equals(model.getMyself().getId())) {
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                    else {
                        setAlignment(Pos.CENTER_LEFT);
                    }
                    label.setText(prepareMessageToBeShown(item));
                    setGraphic(label);
                }
            }
        };
    }
}
