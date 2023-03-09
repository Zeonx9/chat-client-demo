package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.application.ViewHandler;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;

@Getter
public class ChatPageViewModel {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Message> messageListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty messageTextProperty = new SimpleStringProperty();
    private Runnable bottomScroller = () -> {};
    private final ViewHandler viewHandler;
    private final ClientModel model;

    public ChatPageViewModel(ViewHandler viewHandler, ClientModel model) {
        this.viewHandler = viewHandler;
        this.model = model;

        // методы, которые запустит модель во время изменений
        model.addListener("MyChatsUpdate", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("MessageUpdate", runLaterListener(this::updateMessages));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));
        model.addListener("newSelectedMessages", runLaterListener(this::newSelectedMessages));

        // надо новый слушатель для incoming messages, который просто добавляет их в конец
    }
    private void updateMessages(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) event.getNewValue();
        messageListProperty.clear();
        messageListProperty.addAll(messages);
        bottomScroller.run();
    }

    private void newSelectedMessages(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        List<Message> selectedChatMessages = (List<Message>) event.getNewValue();
        messageListProperty.addAll(selectedChatMessages);
        bottomScroller.run();
    }

    private void newChatCreated(PropertyChangeEvent event) {
        Chat chat = (Chat) event.getNewValue();
        chatListProperty.add(chat);

    }

    public <T> void AddBottomScroller(ListView<T> listView) {
        bottomScroller = () -> {
            if (!messageListProperty.isEmpty()) {
                listView.scrollTo(messageListProperty.size() - 1);
            }
        };
    }

    public void onSelectedItemChange(Chat newValue) {
        if (newValue == null) {
            return;
        }
        model.setSelectedChat(newValue);
        model.getMessages();
    }

    public void sendMessage() {
        if (messageTextProperty.get().isBlank()) {
            return;
        }
        model.sendMessageToChat(messageTextProperty.get());
        messageTextProperty.set("");
    }

/* этот метод сейчас не нужен, но может потом пригодится

    public void switchToAllUsers() {
        viewHandler.openView(ViewHandler.Views.ALL_USERS_VIEW);
    }
*/

    private String prepareChatToBeShown(Chat chat) {
        List<String> memberNames = new ArrayList<>();
        chat.getMembers().forEach(member -> {
            if (!Objects.equals(member.getId(), model.getMyself().getId()))
                memberNames.add(member.getUsername());
        });
        var res = String.join(", ", memberNames);
        if (chat.getUnreadCount() != null && chat.getUnreadCount() > 0) {
            res += " (" + chat.getUnreadCount() + ")";
        }
        return res;
    }

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return "from " + msg.getAuthor().getUsername() +
                ": " + msg.getText() +
                " at " + msg.getDateTime().format(dtf);
    }

    public ListCell<Chat> getChatListCellFactory() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                setText(prepareChatToBeShown(item));
                var imgStream = ClientApplication.class.getResourceAsStream("img/user_avatar_chat_icon.png");
                if (imgStream == null) {
                    throw new RuntimeException("image stream is null");
                }
                imageView.setImage(new Image(imgStream));
                setGraphic(imageView);
            }
        };
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
}
