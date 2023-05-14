package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.application.ListenerFactoryAllChecked;
import com.ade.chatclient.viewmodel.LogInViewModel;
import com.ade.chatclient.application.ViewModelUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInView extends AbstractView<LogInViewModel> {
    @FXML private PasswordField passwordField;
    @FXML private TextField loginTextField;
    @FXML private Label errorMessageLabel;
    @FXML private Button loginButton;

    /**
     * метод, который выполняет инициализацию вместо конструктора,
     * так как объекты вью получаться при подключении не через конструктор, а из FXMLLoader
     * выполняет binding модели и вью модели
     * @param logInViewModel ссылка на вью-модель, которая управляет этим вью
     */
    @Override
    public void init(LogInViewModel logInViewModel) {
        super.init(logInViewModel);

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
