package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractChildViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.dtos.GroupRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.GroupCreationDialog;
import com.ade.chatclient.view.cellfactory.ChatListCellFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.util.Optional;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
@Getter
public class AllChatsViewModel extends AbstractChildViewModel<ClientModel> {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public AllChatsViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener("MyChatsUpdate", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));
        model.addListener("selectedChatModified", runLaterListener(this::selectedChatModified));
        model.addListener("UpdateUnreadCount", runLaterListener(this::riseChat));
        model.addListener("MarkAsRead", runLaterListener(this::markAsRead));
        model.addListener("NewMessageInSelectedChat", runLaterListener(this::riseChat));
        model.addListener("UnreadChats", runLaterListener(this::updateUnreadChatsCounter));
    }

    private void updateUnreadChatsCounter(PropertyChangeEvent event) {
        //todo счетчик непрочитанных чатов
        Long unreadChatCounter = (Long) event.getNewValue();
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
        chatListProperty.add(0, chat);
    }

    private void selectedChatModified(PropertyChangeEvent evt) {
        System.out.println("on modification");
        Chat chat = (Chat) evt.getNewValue();
        int index = chatListProperty.indexOf(chat);
        chatListProperty.set(index, chat);
    }

    public void onSelectedItemChange(Chat newValue) {
        if (newValue == null || model.getSelectedChat() != null && model.getSelectedChat().equals(newValue)) {
            return;
        }
        System.out.println("on change of chat selected");
        model.setSelectedChat(newValue);
    }

    public ListCell<Chat> getChatListCellFactory() {
        ChatListCellFactory factory = ViewModelUtils.loadCellFactory(
                ChatListCellFactory.class,
                "chat-list-cell-factory.fxml"
        );
        factory.init(model.getMyself().getId());
        return factory;
    }

    public void showDialogAndWait() {
        GroupCreationDialog dialog = GroupCreationDialog.getInstance();
        dialog.init(model.getAllUsers(), new GroupCreationDialogModel());

        Optional<GroupRequest> answer = dialog.showAndWait();
        System.out.println(answer.orElse(null));
    }
}
