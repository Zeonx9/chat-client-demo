package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.application.AbstractChildViewModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.application.ViewHandler;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
@Getter
public class AllChatsViewModel extends AbstractChildViewModel<ClientModel> {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public AllChatsViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener("MyChatsUpdate", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));
        model.addListener("UpdateUnreadCount", runLaterListener(this::riseChat));
        model.addListener("MarkAsRead", runLaterListener(this::markAsRead));
        model.addListener("NewMessageInSelectedChat", runLaterListener(this::riseChat));
    }

    private void markAsRead(PropertyChangeEvent event) {
        for (Chat chat : chatListProperty) {
            if (chat.equals(event.getNewValue())) {
                chat.setUnreadCount(0L);
                break;
            }
        }
    }

    private void riseChat(PropertyChangeEvent event) {
        chatListProperty.remove((Chat) event.getNewValue());
        chatListProperty.add(0, (Chat) event.getNewValue());
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(true);
    }

    private void newChatCreated(PropertyChangeEvent event) {
        Chat chat = (Chat) event.getNewValue();
        chatListProperty.add(chat);
    }

    public void onSelectedItemChange(Chat newValue) {
        if (newValue == null) {
            return;
        }
        model.setSelectedChat(newValue);
        model.getMessages();
    }

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
}
