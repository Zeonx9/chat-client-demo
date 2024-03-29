package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.viewmodel.ChangingPasswordDialogModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс выступает в роли контроллера для диалогового окна для смены пароля, управляет поведением и отображением элементов на экране
 */
@Setter
@Getter
public class ChangingPasswordDialog extends AbstractDialog<ChangePasswordRequest, ChangingPasswordDialogModel> {
    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private Button changeButton;
    @FXML private Label errorMessage;

    public static ChangingPasswordDialog getInstance() {
        return AbstractDialog.getInstance(ChangingPasswordDialog.class, "changing-password-dialog-view.fxml", "ChatPageViewStyle");
    }

    @Override
    protected void initialize() {
        currentPassword.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onCurPasswordTextChanged));
        currentPassword.textProperty().bindBidirectional(viewModel.getCurPassword());
        newPassword.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onNewPasswordTextChanged));
        newPassword.textProperty().bindBidirectional(viewModel.getNewPassword());
        changeButton.disableProperty().bind(Bindings.or(viewModel.getIsCurPasswordBlank(), viewModel.getIsNewPasswordBlank()));
        errorMessage.textProperty().bind(viewModel.getErrorMessageProperty());
    }

    @Override
    protected String getTitleString() {
        return "Changing password";
    }
}
