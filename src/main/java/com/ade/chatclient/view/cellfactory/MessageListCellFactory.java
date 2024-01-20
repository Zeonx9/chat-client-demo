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

/**
 * Фабрика ячеек списка сообщений, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
@RequiredArgsConstructor
public class MessageListCellFactory extends ListCell<Message> {
    private Long selfId;
    @FXML private VBox wrapper;
    @FXML private AnchorPane layout;
    @FXML private AnchorPane messagePane;
    @FXML private Label messageText;
    @FXML private Label dataText;
    public void init(Long selfId) {
        this.selfId = selfId;
    }

    /**
     * Метод заполняет все значения в полях ячейки, а так же устанавливает layout в качестве графики - AnchorPane, в котором описан интерфейс одной ячейки
     * @param item объект класса Message - сообщение
     * @param empty переменная типа boolean, показывает, является ли ячейка в списке пустой
     */
    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        messageText.setText(prepareMessageToBeShown(item));
        dataText.setText(prepareMessageDataToBeShown(item));
        dataText.setStyle("-fx-font-size: 10; -fx-text-fill: #FFFFFF");
        AnchorPane.clearConstraints(wrapper);

        if (item.getAuthor().getId().equals(selfId)) {
            AnchorPane.setRightAnchor(wrapper, 0.0);
            wrapper.setAlignment(Pos.CENTER_RIGHT);
            messagePane.setStyle("-fx-background-color: #3E46FF");
        }
        else {
            AnchorPane.setLeftAnchor(wrapper, 0.0);
            wrapper.setAlignment(Pos.CENTER_LEFT);
            messagePane.setStyle("-fx-background-color: #212229");
        }
        setGraphic(layout);
    }

    /**
     * @param msg объект класса Message - сообщение
     * @return текст сообщения
     */
    private String prepareMessageToBeShown(Message msg) {
        return msg.getText();
    }

    /**
     * Метод выполняет подготовку данных о сообщении
     * @param msg объект класса Message - сообщение
     * @return автора сообщения и дату и время его отправки в формате "HH:mm, dd.MM"
     */
    private String prepareMessageDataToBeShown(Message msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm, dd.MM");
        if (msg.getAuthor().getId().equals(selfId))
            return msg.getDateTime().format(dtf);
        return msg.getAuthor().getUsername() + ", " + msg.getDateTime().format(dtf);
    }
}

