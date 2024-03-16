package com.ade.chatclient.view;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.application.structure.EmptyDialogModel;
import com.ade.chatclient.application.util.PaneSwitcher;
import com.ade.chatclient.domain.User;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import static com.ade.chatclient.application.Views.PROFILE_VIEW;

public class UserInfoDialog  extends AbstractDialog<User, EmptyDialogModel<User>> {
    @FXML private Pane profilePane;

    public static UserInfoDialog getInstance(){
        UserInfoDialog instance = AbstractDialog.getInstance(UserInfoDialog.class, "user-info-dialog-view.fxml");
        instance.init(new EmptyDialogModel<>());
        return instance;
    }

    public void setProfilePane(ViewHandler viewHandler) {
        PaneSwitcher paneSwitcher = new PaneSwitcher(viewHandler, profilePane);
        paneSwitcher.switchTo(PROFILE_VIEW);
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected String getTitleString() {
        return "User info";
    }
}
