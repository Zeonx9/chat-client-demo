package com.ade.chatclient.view;

import com.ade.chatclient.application.ViewModelUtils;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.viewmodel.ChangingPasswordDialogModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;

public class ChangingPasswordDialog extends Dialog<ChangePasswordRequest> {
    @FXML private DialogPane changePasswordDialog;
    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private Button changeButton;
    @FXML private Label errorMessage;
    private ChangingPasswordDialogModel viewModel;
    public void init(ChangingPasswordDialogModel viewModel) {
        this.viewModel = viewModel;
        setTitle("Changing password");
        setResultConverter(viewModel::resultConverter);
        makeButtonInvisible();

        currentPassword.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onCurPasswordTextChanged));
        newPassword.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onNewPasswordTextChanged));
        changeButton.disableProperty().bind(Bindings.or(viewModel.getIsCurPasswordBlank(), viewModel.getIsNewPasswordBlank()));
        errorMessage.textProperty().bind(viewModel.getErrorMessageProperty());
    }

    public static ChangingPasswordDialog getInstance() {
        FXMLLoader loader = new FXMLLoader(ChangingPasswordDialog.class.getResource("changing-password-dialog-view.fxml"));
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ChangingPasswordDialog controller = loader.getController();
        controller.setDialogPane(pane);
        return controller;
    }

    public void onChangePasswordClicked() {
        setResult(viewModel.onChangeClicked(currentPassword.getText(), newPassword.getText()));
        System.out.println(currentPassword.getText());
        System.out.println(newPassword.getText());
    }
    public void makeButtonInvisible () {
        Node closeButton = changePasswordDialog.lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }
}
