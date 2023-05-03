package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.Message;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class MessageListCellFactory extends ListCell<Message> {
    private Long selfId;
    @FXML private VBox wrapper;
    @FXML private AnchorPane layout;
    @FXML private Label messageText;
    @FXML private Label dataText;
    public void init(Long selfId) {
        this.selfId = selfId;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        messageText.setText(prepareMessageToBeShown(item));
        dataText.setText(prepareMessageDataToBeShown(item));
        AnchorPane.clearConstraints(wrapper);

        if (item.getAuthor().getId().equals(selfId)) {
            AnchorPane.setRightAnchor(wrapper, 0.0);
            wrapper.setAlignment(Pos.CENTER_RIGHT);
        }
        else {
            AnchorPane.setLeftAnchor(wrapper, 0.0);
            wrapper.setAlignment(Pos.CENTER_LEFT);
        }
        setGraphic(layout);
    }

    private String prepareMessageToBeShown(Message msg) {
        return msg.getText();
    }

    private String prepareMessageDataToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return msg.getAuthor().getUsername() + ", " + msg.getDateTime().format(dtf);
    }
}

