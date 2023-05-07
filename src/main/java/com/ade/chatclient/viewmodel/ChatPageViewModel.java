package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.application.Views;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.GroupInfoDialog;
import com.ade.chatclient.view.cellfactory.MessageListCellFactory;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;

@Getter
public class ChatPageViewModel extends AbstractViewModel<ClientModel> {
    private final ListProperty<Message> messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private final BooleanProperty showChatsButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty showUsersButtonDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty showUserProfileDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty infoButtonFocusProperty = new SimpleBooleanProperty(false);
    private final StringProperty userNameProperty = new SimpleStringProperty();
    private final DoubleProperty opacityProperty = new SimpleDoubleProperty(0);
    private Runnable bottomScroller;
    private Consumer<Views> paneSwitcher;

    public ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener("gotMessages", runLaterListener(this::fillMessageHistory));
        model.addListener("newMessagesInSelected", runLaterListener(this::newSelectedMessages));
    }

    private void fillMessageHistory(PropertyChangeEvent event) {
        synchronized (messageListProperty) {
            @SuppressWarnings("unchecked")
            List<Message> messages = (List<Message>) event.getNewValue();
            messageListProperty.clear();
            messageListProperty.addAll(messages);
            userNameProperty.setValue(model.getSelectedChat().getChatName());
            bottomScroller.run();
        }

        if (model.getSelectedChat().getIsPrivate()) opacityProperty.set(0);
        else opacityProperty.set(100);
    }

    private void newSelectedMessages(PropertyChangeEvent event) {
        synchronized (messageListProperty) {
            @SuppressWarnings("unchecked")
            List<Message> selectedChatMessages = (List<Message>) event.getNewValue();
            messageListProperty.addAll(selectedChatMessages);
            bottomScroller.run();
        }
    }

    public <T> void addBottomScroller(ListView<T> listView) {
        synchronized (messageListProperty) {
            bottomScroller = () -> {
                if (!messageListProperty.isEmpty()) {
                    listView.scrollTo(messageListProperty.size() - 1);
                }
            };
        }
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
    public void openProfilePane() {
        paneSwitcher.accept(Views.USER_PROFILE_VIEW);
    }

    public void sendMessage() {
        if (Objects.equals(messageTextProperty.getValue(), null) || messageTextProperty.get().isBlank()) {
            return;
        }

        String message = messageTextProperty.get();
        if (message.length() <= 250) {
            model.sendMessageToChat(message);
        }
        else {
            int startIndex = 0;
            while (startIndex < message.length()) {
                int endIndex = Math.min(startIndex + 250, message.length());
                if (endIndex < message.length() && message.charAt(endIndex) != ' ' && message.charAt(endIndex - 1) != ' ') {
                    int lastSpaceIndex = message.lastIndexOf(' ', endIndex);
                    if (lastSpaceIndex != -1 && lastSpaceIndex > startIndex)
                        endIndex = lastSpaceIndex;
                }
                    model.sendMessageToChat(message.substring(startIndex, endIndex));
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                startIndex = endIndex;
            }
        }
        messageTextProperty.set("");
    }

    public void changeButtonsParam(Integer index) {
        showUserProfileDisabled.set(index == 0);
        showUsersButtonDisabled.set(index == 1);
        showChatsButtonDisabled.set(index == 2);
    }

    public ListCell<Message> getMessageCellFactory() {
        MessageListCellFactory factory = ViewModelUtils.loadCellFactory(
                MessageListCellFactory.class,
                "message-list-cell-factory.fxml"
        );
        factory.init(model.getMyself().getId());
        return factory;
    }

    public void showDialog() {
        if (model.getSelectedChat().getIsPrivate()) return;
        GroupInfoDialog dialog = GroupInfoDialog.getInstance();
        dialog.init(model.getSelectedChat());
        dialog.showAndWait();
    }
}
