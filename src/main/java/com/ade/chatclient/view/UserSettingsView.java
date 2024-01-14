package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.viewmodel.UserSettingsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Класс выступает в роли контроллера для панели личного кабинета пользователя, управляет поведением и отображением элементов на экране
 */
public class UserSettingsView extends AbstractView<UserSettingsViewModel> {
    @FXML private Pane profilePane;
    @FXML private Label passwordResultMessage;

    @Override
    protected void initialize() {
        passwordResultMessage.textProperty().bind(viewModel.getResultMessageProperty());

        viewModel.openUserProfile(profilePane);
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для смены пароля, собирает данные от пользователя
     * и отправляет запрос на изменения пароля,
     * срабатывает по нажатию на кнопку Change password
     */
    public void onChangeButtonClicked() {
        viewModel.showDialogAndWait();
    }

    /**
     * Функция запускает процесс выхода из аккаунта,
     * срабатывает при нажатии на кнопку Log Out
     */
    public void onLogOutButtonClicked() {
        viewModel.logOut();
    }

    /**
     * Метод вызывает функцию открытия диалогового окна для редактирования личного профиля пользователя
     */
    public void onEditProfileButtonClicked() {return;}
}
