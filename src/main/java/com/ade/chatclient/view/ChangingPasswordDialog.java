package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractDialog;
import com.ade.chatclient.application.ViewModelUtils;
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

    /**
     * Вызывает метод инициализации из абстрактного класса, а так же связывает все поля интерфейса диалогового окна с соответствующими property в ChangingPasswordDialogModel
     * @param viewModel класс DialogModel, соответствующий типу диалогового окна
     */
    @Override
    public void init(ChangingPasswordDialogModel viewModel) {
        super.init(viewModel);
        setTitle("Changing password");

        currentPassword.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onCurPasswordTextChanged));
        currentPassword.textProperty().bindBidirectional(viewModel.getCurPassword());
        newPassword.textProperty().addListener(ViewModelUtils.changeListener(viewModel::onNewPasswordTextChanged));
        newPassword.textProperty().bindBidirectional(viewModel.getNewPassword());
        changeButton.disableProperty().bind(Bindings.or(viewModel.getIsCurPasswordBlank(), viewModel.getIsNewPasswordBlank()));
        errorMessage.textProperty().bind(viewModel.getErrorMessageProperty());
    }

    public static ChangingPasswordDialog getInstance() {
        return AbstractDialog.getInstance(ChangingPasswordDialog.class, "changing-password-dialog-view.fxml");
    }
}
