package com.ade.chatclient.view.cellfactory;

import com.ade.chatclient.application.Settings;
import com.ade.chatclient.application.SettingsManager;
import com.ade.chatclient.domain.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Фабрика ячеек списка сообщений, предназначена для генерации и настройки ячеек в ListView, определяет, как они будут выглядеть для дальнейшей автоматической генерации
 */
@RequiredArgsConstructor
public class MessageListCellFactory extends ListCell<Message> {
    private Long selfId;
    private Function<String, CompletableFuture<Image>> request;
    @FXML
    private VBox wrapper;
    @FXML
    private AnchorPane layout;
    @FXML
    private VBox messagePane;
    @FXML
    private ImageView photoPane;
    @FXML
    private Label messageText;
    @FXML
    private Label dataText;

    public void init(Long selfId, Function<String, CompletableFuture<Image>> imageRequest) {
        this.selfId = selfId;
        this.request = imageRequest;
    }

    /**
     * Метод заполняет все значения в полях ячейки, а так же устанавливает layout в качестве графики - AnchorPane, в котором описан интерфейс одной ячейки
     *
     * @param item  объект класса Message - сообщение
     * @param empty переменная типа boolean, показывает, является ли ячейка в списке пустой
     */
    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        photoPane.setImage(null);
        photoPane.setFitHeight(0);
        photoPane.setFitWidth(0);
        messageText.setAlignment(null);

        if (item.getIsAuxiliary() != null && item.getIsAuxiliary()) {
            messageText.setText(prepareMessageToBeShown(item));
            dataText.setText("");

            AnchorPane.clearConstraints(wrapper);

            messagePane.setStyle("-fx-background-color: transparent");
            messageText.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #212229; -fx-background-radius: 5px; -fx-padding: 2px;");
            messagePane.setAlignment(Pos.CENTER);
            messageText.setAlignment(Pos.CENTER);

            wrapper.setAlignment(Pos.CENTER);
            AnchorPane.setRightAnchor(wrapper, 0.0);
            AnchorPane.setLeftAnchor(wrapper, 0.0);
        } else {
            Settings settings = SettingsManager.getSettings();

            messageText.setText(prepareMessageToBeShown(item));
            dataText.setText(prepareMessageDataToBeShown(item));

            AnchorPane.clearConstraints(wrapper);

            if (item.getAuthor().getId().equals(selfId)) {
                AnchorPane.setRightAnchor(wrapper, 0.0);
                wrapper.setAlignment(Pos.CENTER_RIGHT);
                messagePane.setStyle("-fx-background-color: #3E46FF");
                messageText.setStyle("-fx-text-fill: #FFFFFF");
            } else {
                AnchorPane.setLeftAnchor(wrapper, 0.0);
                wrapper.setAlignment(Pos.CENTER_LEFT);
                if (Objects.equals(settings.getTheme(), "Light")) {
                    messagePane.setStyle("-fx-background-color: #FFFFFF");
                    messageText.setStyle("-fx-text-fill: #212229");
                } else {
                    messagePane.setStyle("-fx-background-color: #212229");
                    messageText.setStyle("-fx-text-fill: #FFFFFF");
                }
            }

            if (item.getAttachmentId() != null) {
                photoPane.setFitHeight(300);
                CompletableFuture<Image> future = request.apply(item.getAttachmentId());
                future.thenAccept(image -> {
                    Platform.runLater(() -> {
                        scaleImage(image);
                        setGraphic(layout);
                    });
                });
            }
        }
        setGraphic(layout);
    }

    private void scaleImage(Image image) {
        double oldWidth = image.getWidth();
        double oldHeight = image.getHeight();

        double ratioX = (double) 350 / oldWidth;
        double ratioY = (double) 300 / oldHeight;
        double ratio = Math.min(ratioX, ratioY);

        int newWidthInt = (int) (oldWidth * ratio);
        int newHeightInt = (int) (oldHeight * ratio);

        photoPane.setFitWidth(newWidthInt);
        photoPane.setFitHeight(300);
        photoPane.setImage(image);
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
     *
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

