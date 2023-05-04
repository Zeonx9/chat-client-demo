package com.ade.chatclient.view;

import com.ade.chatclient.application.AbstractView;
import com.ade.chatclient.viewmodel.UserProfileViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserProfileView extends AbstractView<UserProfileViewModel> {
    @FXML private Label fullName;
    @FXML private Label birthDate;
    @FXML private Label userName;
    @FXML private Label companyName;
    @Override
    public void init(UserProfileViewModel viewModel) {
        super.init(viewModel);

        fullName.textProperty().bind(viewModel.getFullNameProperty());
        birthDate.textProperty().bind(viewModel.getBirthDateProperty());
        userName.textProperty().bind(viewModel.getUserNameProperty());
        companyName.textProperty().bind(viewModel.getCompanyNameProperty());

        viewModel.getPersonalUserData();
    }

    public void onChangeButtonClicked() {
        viewModel.showDialogAndWait();
    }
}
