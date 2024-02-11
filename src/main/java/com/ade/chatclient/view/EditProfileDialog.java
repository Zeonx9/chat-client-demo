package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.EditProfileDialogModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditProfileDialog extends AbstractDialog<User, EditProfileDialogModel> {
    @FXML private TextField firstNameTextField;
    @FXML private TextField surnameTextField;
    @FXML private TextField patronymicTextField;
    @FXML private TextField phoneNumberTextField;
    @FXML private TextField birthdayTextField;

    public static EditProfileDialog getInstance() {
        return AbstractDialog.getInstance(EditProfileDialog.class, "edit-profile-dialog-view.fxml", "ChatPageViewStyle");
    }
    @Override
    protected void initialize() {
        firstNameTextField.textProperty().bindBidirectional(viewModel.getFirstNameProperty());
        surnameTextField.textProperty().bindBidirectional(viewModel.getSurnameProperty());
        patronymicTextField.textProperty().bindBidirectional(viewModel.getPatronymicProperty());
        phoneNumberTextField.textProperty().bindBidirectional(viewModel.getPhoneNumberProperty());
        birthdayTextField.textProperty().bindBidirectional(viewModel.getBirthdayProperty());
    }

    @Override
    protected String getTitleString() {
        return "Edit profile";
    }
}
