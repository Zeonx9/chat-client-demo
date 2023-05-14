package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.util.ListenerFactoryAllChecked;
import com.ade.chatclient.viewmodel.AdminViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Класс выступает в роли контроллера для панели админа, управляет поведением и отображением элементов на экране
 */
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
    protected void initialize() {
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

    /**
     * Функция запускает процесс выхода из аккаунта,
     * срабатывает при нажатии на кнопку Log Out
     */
    public void onLogOutClicked() {
        viewModel.logOut();
    }

    /**
     * Метод создает запрос на регистрацию нового пользователя в компании и отображает на View результат и данные от аккаунта нового пользователя в случае успешного ответа от сервера, срабатывает при нажатии на кнопку Register an employee
     */
    public void onRegisterEmpClicked() { result.setText(viewModel.register());
    }
}
