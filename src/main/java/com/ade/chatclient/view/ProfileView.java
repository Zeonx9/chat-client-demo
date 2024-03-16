package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.viewmodel.ProfileViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ProfileView extends AbstractView<ProfileViewModel> {
    @FXML private Label fullNameLabel;
    @FXML private Label loginLabel;
    @FXML private Label onlineStatusLogin;
    @FXML private Label phoneNumber;
    @FXML private Label birthDate;
    @FXML private Label companyName;
    @FXML private Label initials;
    @FXML private StackPane photoPane;

    @Override
    protected void initialize() {
        fullNameLabel.textProperty().bind(viewModel.getFullNameProperty());
        loginLabel.textProperty().bind(viewModel.getLoginProperty());
        onlineStatusLogin.textProperty().bind(viewModel.getStatusProperty());
        phoneNumber.textProperty().bind(viewModel.getPhoneNumberProperty());
        birthDate.textProperty().bind(viewModel.getBirthDateProperty());
        companyName.textProperty().bind(viewModel.getCompanyNameProperty());
        initials.textProperty().bind(viewModel.getInitialsProperty());

        viewModel.setUserPersonalData();
        fillUserPhoto();
    }

    private void fillUserPhoto() {
        Circle circle = new Circle(40, Color.rgb(145, 145, 145));
        photoPane.getChildren().addAll(circle);
    }
}
