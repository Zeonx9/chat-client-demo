package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.LogInViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;

@Getter
public class LogInView {
    @FXML public javafx.scene.control.PasswordField PasswordField;
    @FXML private TextField loginTextField;
    @FXML private Label errorMessageLabel;
    @FXML private Button loginButton;

    private LogInViewModel viewModel;

    /**
     * метод, который выполняет инициализацию вместо конструкора,
     * так как объекты вью получаются при подключении не через констуктор, а из FXMLLoader
     * выполняет байндинг модели и вью модели
     * @param logInViewModel ссылка на вью-модель, которая управляет этим вью
     */
    public void init(LogInViewModel logInViewModel) {
        this.viewModel = logInViewModel;

        loginTextField.textProperty().bindBidirectional(viewModel.getLoginTextProperty());
        loginTextField.textProperty().addListener(viewModel::onTextChanged);

        errorMessageLabel.textProperty().bind(viewModel.getErrorMessageProperty());
        loginButton.disableProperty().bind(viewModel.getDisableButtonProperty());
    }

    @FXML
    protected void onLoginClick() {
        viewModel.authorize();
    }
}
