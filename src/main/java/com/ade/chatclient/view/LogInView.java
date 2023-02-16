package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.LogInViewModel;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.Objects;

@Getter
public class LogInView {
    @FXML private PasswordField passwordField;
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
        loginTextField.setFocusTraversable(false);

        passwordField.textProperty().bindBidirectional(viewModel.getPasswordProperty());
        passwordField.textProperty().addListener(viewModel::onTextChanged);
        passwordField.setFocusTraversable(false);

        errorMessageLabel.textProperty().bind(viewModel.getErrorMessageProperty());
        if (Objects.equals(viewModel.getErrorMessageProperty().toString(), "Success!")) {
            errorMessageLabel.setTextFill(Color.color(0, 1, 0));
        }
        else {
            errorMessageLabel.setTextFill(Color.color(1, 0, 0));
        }

        loginButton.disableProperty().bind(viewModel.getDisableButtonProperty());
    }

    @FXML
    protected void onLoginClick() {
        viewModel.authorize();
    }
}
