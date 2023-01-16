package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.LogInViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LogInView {
    private LogInViewModel viewModel;

    @FXML
    private TextField loginTextField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button loginButton;

    @FXML
    protected void onLoginClick() {
        viewModel.authorize();
    }

    public void init(LogInViewModel logInViewModel) {
        this.viewModel = logInViewModel;
        loginTextField.textProperty().bindBidirectional(viewModel.getLoginProperty());
        loginTextField.textProperty().addListener(viewModel::onTextChanged);

        errorMessageLabel.textProperty().bind(viewModel.getErrorMessageProperty());
        loginButton.disableProperty().bind(viewModel.getDisableProperty());
    }
}
