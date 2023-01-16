package com.ade.chatclient.view;

import javafx.scene.control.TextField;

public class RegisterView {
    public TextField login;

    public String UserLogin;

    public void registered() {
        UserLogin = login.getText();
        // there should be a transition to another screen after registration
    }
}
