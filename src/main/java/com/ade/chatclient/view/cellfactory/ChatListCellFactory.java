package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListCellFactory extends ListCell<Chat> {
    private Long selfId;
    @FXML private AnchorPane layout;
    @FXML private Label chatNameLabel;
    @FXML private Label lastMsgLabel;
    @FXML private Label countUnreadMessages;

    public void init(Long selfId) {
        this.selfId = selfId;
    }

    @Override
    protected void updateItem(Chat item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        chatNameLabel.setText(prepareChatToBeShown(item));
        lastMsgLabel.setText(prepareLastMessage(item));

        if (item.getUnreadCount() != 0) {
            countUnreadMessages.setText(String.valueOf(item.getUnreadCount()));
            countUnreadMessages.setStyle("-fx-background-color: #3D77A3");
        }
        else {
            countUnreadMessages.setText("");
            countUnreadMessages.setStyle("-fx-background-color: transparent");
        }

        setGraphic(layout);
    }

    private String prepareChatToBeShown(Chat chat) {
        if (chat.getGroup() != null) {
            return chat.getGroup().getName();
        }

        List<String> memberNames = new ArrayList<>();
        chat.getMembers().forEach(member -> {
            if (!Objects.equals(member.getId(), selfId))
                memberNames.add(member.getRealName() + " " + member.getSurname());
        });
        return String.join(", ", memberNames);
    }

    private String prepareLastMessage(Chat chat) {
        Message msg = chat.getLastMessage();
        if (msg == null) {
            return null;
        }
        if (msg.getText().length() < 20) {
            return msg.getText();
        }
        return msg.getText().substring(0, 20) + "...";
    }
}
