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

        model.addListener("gotChats", runLaterListener(listReplacer(chatListProperty)));
        model.addListener("NewChatCreated", runLaterListener(this::newChatCreated));
        model.addListener("selectedChatModified", runLaterListener(this::selectedChatModified));
        model.addListener("chatReceivedMessages", runLaterListener(this::raiseChat));
        model.addListener("UnreadChats", runLaterListener(this::updateUnreadChatsCounter));
    }

    private void updateUnreadChatsCounter(PropertyChangeEvent event) {
        //todo счетчик непрочитанных чатов
        Long unreadChatCounter = (Long) event.getNewValue();
    }

    private void raiseChat(PropertyChangeEvent event) {
        Chat chat = (Chat) event.getNewValue();
        chatListProperty.remove(chat);
        chatListProperty.add(0, chat);
    }

    private void newChatCreated(PropertyChangeEvent event) {
        Chat chat = (Chat) event.getNewValue();
        chatListProperty.add(0, chat);
    }

    private void selectedChatModified(PropertyChangeEvent evt) {
        Chat chat = (Chat) evt.getNewValue();
        int index = chatListProperty.indexOf(chat);
        chatListProperty.set(index, chat);
    }

    public void onSelectedItemChange(Chat changedChat) {
        if (changedChat == null || changedChat.equals(model.getSelectedChat())) {
            return;
        }
        model.selectChat(changedChat);
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(2);
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
        if (answer.isPresent()) {
            System.out.println(answer.get());
            model.createGroupChat(answer.get());
        }
    }
}
