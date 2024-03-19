package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.domain.EditProfileResult;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class EditProfileDialogModel extends AbstractDialogModel<EditProfileResult> {
    private final ObservableList<Node> chatIconNodes = FXCollections.observableArrayList();
    private final StringProperty firstNameProperty = new SimpleStringProperty();
    private final StringProperty surnameProperty = new SimpleStringProperty();
    private final StringProperty patronymicProperty = new SimpleStringProperty();
    private final StringProperty phoneNumberProperty = new SimpleStringProperty();
    private final StringProperty birthdayProperty = new SimpleStringProperty();
    @Setter
    private static User mySelf = new User();
    private File file;

    @Override
    public EditProfileResult resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate birthDate = birthdayProperty.getValue() != null ?
                LocalDate.parse(birthdayProperty.getValue(), formatter) : null;

        User updatedUser = User.builder()
                .realName(firstNameProperty.getValue())
                .surname(surnameProperty.getValue())
                .patronymic(patronymicProperty.getValue())
                .phoneNumber(phoneNumberProperty.getValue())
                .dateOfBirth(birthDate).build();

        return EditProfileResult.builder().user(updatedUser).file(file).build();
    }

    public void setUserPersonalData() {
        firstNameProperty.set(mySelf.getRealName());
        surnameProperty.set(mySelf.getSurname());
        patronymicProperty.set(mySelf.getPatronymic());
        phoneNumberProperty.set(mySelf.getPhoneNumber());

        chatIconNodes.clear();
        UserPhoto.setPaneContent(chatIconNodes, mySelf, 40);
    }

    public void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Изображения (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.file = file;
        }
    }


}
