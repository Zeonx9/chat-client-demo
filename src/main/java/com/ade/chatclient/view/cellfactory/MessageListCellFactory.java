package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.domain.Message;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class MessageListCellFactory extends ListCell<Message> {
    private final Label label = new Label();
    private final Long selfId;

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        label.setText(prepareMessageToBeShown(item));
        setAlignment(item.getAuthor().getId().equals(selfId) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        setGraphic(label);
    }

    private String prepareMessageToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        return msg.getText() + " at " + msg.getDateTime().format(dtf);
    }
}

