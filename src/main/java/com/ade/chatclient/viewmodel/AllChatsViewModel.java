package com.ade.chatclient.viewmodel;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
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

import static com.ade.chatclient.viewmodel.ViewModelUtils.listReplacer;
import static com.ade.chatclient.viewmodel.ViewModelUtils.runLaterListener;

public class AllChatsViewModel {
    private final ListProperty<Chat> chatListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Runnable bottomScroller = () -> {};
    private final ViewHandler viewHandler;
    private final ClientModel model;

    AllChatsViewModel(ViewHandler viewHandler, ClientModel model) {
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
        List<Message> messages = (List<Message>) event.getNewValue();
        messageListProperty.clear();
        messageListProperty.addAll(messages);
        bottomScroller.run();
    }

    private void newSelectedMessages(PropertyChangeEvent event) {
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
}
