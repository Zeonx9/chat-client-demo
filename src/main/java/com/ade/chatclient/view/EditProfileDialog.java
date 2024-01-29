package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.EditProfileDialogModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditProfileDialog extends AbstractDialog<User, EditProfileDialogModel> {
    @FXML private TextField firstNameTextField;
    @FXML private TextField surnameTextField;
    @FXML private TextField patronymicTextField;
    @FXML private TextField phoneNumberTextField;
    @FXML private TextField birthdayTextField;

    public static EditProfileDialog getInstance() {
        return AbstractDialog.getInstance(EditProfileDialog.class, "edit-profile-dialog-view.fxml");
    }
    @Override
    protected void initialize() {
        firstNameTextField.textProperty().bind(viewModel.getFirstNameProperty());
        surnameTextField.textProperty().bind(viewModel.getSurnameProperty());
        patronymicTextField.textProperty().bind(viewModel.getPatronymicProperty());
        phoneNumberTextField.textProperty().bind(viewModel.getPhoneNumberProperty());
        birthdayTextField.textProperty().bind(viewModel.getBirthdayProperty());
    }

    @Override
    protected String getTitleString() {
        return "Edit profile";
    }
}
