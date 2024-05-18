package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.EditChatResult;
import com.ade.chatclient.dtos.ChangeGroupName;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class EditChatDialogModel extends AbstractDialogModel<EditChatResult> {
    private final ObservableList<Node> chatIconNodes = FXCollections.observableArrayList();
    private final StringProperty chatNameProperty = new SimpleStringProperty();
    private final Function<String, CompletableFuture<Image>> imageRequest;
    private File file;
    @Setter
    private static Long mySelfId;
    @Setter
    private static Chat currentChat;

    @Override
    public EditChatResult resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }

        ChangeGroupName changeGroupName = new ChangeGroupName(chatNameProperty.getValue());

        return EditChatResult.builder().chatId(currentChat.getId())
                .changeGroupName(changeGroupName).file(file).build();
    }

    public void setChatData() {
        chatNameProperty.set(currentChat.getChatName(mySelfId));
        chatIconNodes.clear();
        Objects.requireNonNull(UserPhoto.getPaneContent(currentChat, mySelfId, 40, imageRequest))
                .thenAccept(children -> {
                    Platform.runLater(() -> {
                        chatIconNodes.clear();
                        chatIconNodes.addAll(children);
                    });
                });
    }

    public void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Изображения (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.file = file;

            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40 * 2);
            imageView.setFitHeight(40 * 2);
            Circle circle = new Circle(40);
            circle.setCenterX(40);
            circle.setCenterY(40);
            imageView.setClip(circle);

            chatIconNodes.clear();
            chatIconNodes.add(imageView);
        }
    }
}
