package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.application.ListenerFactoryAllChecked;
import com.ade.chatclient.viewmodel.AdminViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AdminView extends AbstractView<AdminViewModel> {
    @FXML private Label adminName;
    @FXML private Label adminCompany;
    @FXML private TextField nameAndSurnameEmp;
    @FXML private DatePicker birthdateEmp;
    @FXML private TextField loginEmp;
    @FXML private Button registerButton;
    @FXML private Label result;
    @FXML private Label resultLogin;
    @FXML private Label resultPassword;

    @Override
    public void init(AdminViewModel viewModel) {
        super.init(viewModel);

        var listenerFactory = new ListenerFactoryAllChecked(viewModel::onCheckPassed, viewModel::onCheckFailed);
        nameAndSurnameEmp.textProperty().addListener(listenerFactory.newListener(viewModel::checkNameChangedText));
        birthdateEmp.valueProperty().addListener(listenerFactory.newListener(viewModel::checkDataChanged));
        loginEmp.textProperty().addListener(listenerFactory.newListener(viewModel::checkLoginChangedText));

        adminName.setText(viewModel.getMyName());
        adminCompany.setText(viewModel.getMyCompany());

        nameAndSurnameEmp.textProperty().bindBidirectional(viewModel.getEmpNameAndSurnameProperty());
        birthdateEmp.valueProperty().bindBidirectional(viewModel.getEmpBirthdateProperty());
        loginEmp.textProperty().bindBidirectional(viewModel.getEmpLoginProperty());
        registerButton.disableProperty().bind(viewModel.getDisableProperty());

        resultLogin.textProperty().bind(viewModel.getResultLoginProperty());
        resultPassword.textProperty().bind(viewModel.getResultPasswordProperty());

    }

    public void onLogOutClicked() {
        viewModel.logOut();
    }

    public void onRegisterEmpClicked() { result.setText("Result: " + viewModel.register());
    }
}
