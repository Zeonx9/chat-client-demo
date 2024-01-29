package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.domain.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ButtonType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class EditProfileDialogModel extends AbstractDialogModel<User> {
    private final StringProperty firstNameProperty = new SimpleStringProperty();
    private final StringProperty surnameProperty = new SimpleStringProperty();
    private final StringProperty patronymicProperty = new SimpleStringProperty();
    private final StringProperty phoneNumberProperty = new SimpleStringProperty();
    private final StringProperty birthdayProperty = new SimpleStringProperty();

    @Override
    public User resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate birthDate = birthdayProperty.getValue() != null ?
                LocalDate.parse(birthdayProperty.getValue(), formatter) : null;

        return User.builder()
                .realName(firstNameProperty.getValue())
                .surname(surnameProperty.getValue())
                .patronymic(patronymicProperty.getValue())
                .phoneNumber(phoneNumberProperty.getValue())
                .dateOfBirth(birthDate).build();
    }
}
