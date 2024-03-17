package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.viewmodel.ProfileViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ProfileView extends AbstractView<ProfileViewModel> {
    @FXML private Label fullNameLabel;
    @FXML private Label loginLabel;
    @FXML private Label onlineStatusLogin;
    @FXML private Label phoneNumber;
    @FXML private Label birthDate;
    @FXML private Label companyName;
    @FXML private StackPane photoPane;

    @Override
    protected void initialize() {
        fullNameLabel.textProperty().bind(viewModel.getFullNameProperty());
        loginLabel.textProperty().bind(viewModel.getLoginProperty());
        onlineStatusLogin.textProperty().bind(viewModel.getStatusProperty());
        phoneNumber.textProperty().bind(viewModel.getPhoneNumberProperty());
        birthDate.textProperty().bind(viewModel.getBirthDateProperty());
        companyName.textProperty().bind(viewModel.getCompanyNameProperty());
        Bindings.bindContentBidirectional(photoPane.getChildren(), viewModel.getChatIconNodes());

        viewModel.setUserPersonalData();
    }
}
