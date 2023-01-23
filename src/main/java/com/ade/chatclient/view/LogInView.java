package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.LogInViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;

@Getter
public class LogInView {
    @FXML private TextField loginTextField;
    @FXML private Label errorMessageLabel;
    @FXML private Button loginButton;

    private LogInViewModel viewModel;

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
