package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.domain.EditProfileResult;
import com.ade.chatclient.domain.User;
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
public class EditProfileDialogModel extends AbstractDialogModel<EditProfileResult> {
    private final ObservableList<Node> chatIconNodes = FXCollections.observableArrayList();
    private final StringProperty firstNameProperty = new SimpleStringProperty();
    private final StringProperty surnameProperty = new SimpleStringProperty();
    private final StringProperty patronymicProperty = new SimpleStringProperty();
    private final StringProperty phoneNumberProperty = new SimpleStringProperty();
    private final StringProperty birthdayProperty = new SimpleStringProperty();
    private final Function<String, CompletableFuture<Image>> imageRequest;
    @Setter
    private static User mySelf = new User();
    private File file;

    @Override
    public EditProfileResult resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }

        User updatedUser = User.builder()
                .realName(firstNameProperty.getValue())
                .surname(surnameProperty.getValue())
                .patronymic(patronymicProperty.getValue())
                .phoneNumber(phoneNumberProperty.getValue()).build();

        return EditProfileResult.builder().user(updatedUser).file(file).build();
    }

    public void setUserPersonalData() {
        firstNameProperty.set(mySelf.getRealName());
        surnameProperty.set(mySelf.getSurname());
        patronymicProperty.set(mySelf.getPatronymic());
        phoneNumberProperty.set(mySelf.getPhoneNumber());

        chatIconNodes.clear();
        Objects.requireNonNull(UserPhoto.getPaneContent(mySelf, 40, imageRequest))
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
        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter("Изображения (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.file = file;

            chatIconNodes.clear();
            chatIconNodes.add(prepareImageToShow(new Image(file.toURI().toString())));
        }
    }

    private ImageView prepareImageToShow(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40 * 2);
        imageView.setFitHeight(40 * 2);
        Circle circle = new Circle(40);
        circle.setCenterX(40);
        circle.setCenterY(40);
        imageView.setClip(circle);
        return imageView;
    }

}
