package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.util.ListenerFactoryAllChecked;
import com.ade.chatclient.viewmodel.LogInViewModel;
import com.ade.chatclient.application.util.ViewModelUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс выступает в роли контроллера для окна авторизации, управляет поведением и отображением элементов на экране
 */
@Getter
@Setter
public class LogInView extends AbstractView<LogInViewModel> {
    @FXML private PasswordField passwordField;
    @FXML private TextField loginTextField;
    @FXML private Label errorMessageLabel;
    @FXML private Button loginButton;

    @Override
    protected void initialize() {
        var listenerFactory = new ListenerFactoryAllChecked(viewModel::onCheckPassed, viewModel::onCheckFailed);
        loginTextField.textProperty().addListener(listenerFactory.newListener(viewModel::checkChangedText));
        passwordField.textProperty().addListener(listenerFactory.newListener(viewModel::checkChangedText));

        loginTextField.textProperty().bindBidirectional(viewModel.getLoginTextProperty());
        passwordField.textProperty().bindBidirectional(viewModel.getPasswordProperty());
        errorMessageLabel.textProperty().bind(viewModel.getErrorMessageProperty());
        loginButton.disableProperty().bind(viewModel.getDisableButtonProperty());

        viewModel.fillSavedLoginAndPassword();

        passwordField.setOnKeyPressed(ViewModelUtils.enterKeyHandler(viewModel::authorize));
    }

    /**
     * Метод, который вызывает функцию авторизации пользователя в системе и переключения на ChatPage или на AdminView в зависимости от аккаунта, под которым заходит пользователь.
     * Срабатывает по нажатию на кнопку Log In
     */
    @FXML
    protected void onLoginClick() {
        viewModel.authorize();
    }
}
