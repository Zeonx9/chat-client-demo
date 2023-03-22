package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Objects;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
@Getter
public class AllChatsViewModel {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ViewHandler viewHandler;
    private final ClientModel model;

    public AllChatsViewModel(ViewHandler viewHandler, ClientModel model) {
        this.viewHandler = viewHandler;
        this.model = model;

        // методы, которые запустит модель во время изменений
        model.addListener("MyChatsUpdate", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));

        // надо новый слушатель для incoming messages, который просто добавляет их в конец
    }

    private void newChatCreated(PropertyChangeEvent event) {
        Chat chat = (Chat) event.getNewValue();
        chatListProperty.add(chat);
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
    public void onSelectedItemChange(Chat newValue) {
        if (newValue == null) {
            return;
        }
        model.setSelectedChat(newValue);
        model.getMessages();
    }

    private String prepareChatToBeShown(Chat chat) {
        List<String> memberNames = chat.getMembers().stream()
                .filter(this::isNotMyself)
                .map(User::getUsername)
                .toList();
        var res = String.join(", ", memberNames);
        if (chat.getUnreadCount() != null && chat.getUnreadCount() > 0) {
            res += " (" + chat.getUnreadCount() + ")";
        }
        return res;
    }

    private boolean isNotMyself(User user) {
        return !Objects.equals(user.getId(), model.getMyself().getId());
    }
}
