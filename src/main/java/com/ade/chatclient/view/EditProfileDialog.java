package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.domain.EditProfileResult;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.EditProfileDialogModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class EditProfileDialog extends AbstractDialog<EditProfileResult, EditProfileDialogModel> {
    @FXML private TextField firstNameTextField;
    @FXML private TextField surnameTextField;
    @FXML private TextField patronymicTextField;
    @FXML private TextField phoneNumberTextField;
//    @FXML private TextField birthdayTextField;
    @FXML private StackPane photoPane;

    public static EditProfileDialog getInstance(User user) {
        EditProfileDialogModel.setMySelf(user);
        return AbstractDialog.getInstance(EditProfileDialog.class, "edit-profile-dialog-view.fxml", "ChatPageViewStyle");
    }
    @Override
    protected void initialize() {
        firstNameTextField.textProperty().bindBidirectional(viewModel.getFirstNameProperty());
        surnameTextField.textProperty().bindBidirectional(viewModel.getSurnameProperty());
        patronymicTextField.textProperty().bindBidirectional(viewModel.getPatronymicProperty());
        phoneNumberTextField.textProperty().bindBidirectional(viewModel.getPhoneNumberProperty());
//        birthdayTextField.textProperty().bindBidirectional(viewModel.getBirthdayProperty());
        Bindings.bindContentBidirectional(photoPane.getChildren(), viewModel.getChatIconNodes());

        viewModel.setUserPersonalData();
    }

    @Override
    protected String getTitleString() {
        return "Edit profile";
    }

    @FXML protected void onChangePhotoClicked() {
        viewModel.openFileChooser();
    }
}
