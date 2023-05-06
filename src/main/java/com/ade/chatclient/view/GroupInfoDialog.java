package com.ade.chatclient.view;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;

public class GroupInfoDialog extends Dialog<GroupChatInfo> {
    @FXML private Label groupInfo;
    @FXML private DialogPane groupInfoDialog;
    @FXML private Label membersCount;
    @FXML private ListView<User> listMembers;

    public void init(Chat chat) {
        setTitle("Group info");
        makeButtonInvisible();

        groupInfo.setText("Group info     '" + chat.getChatName() + "'");
        membersCount.setText(chat.getMembers().size() + " members");
        listMembers.getItems().setAll(chat.getMembers());
        listMembers.setCellFactory(param -> AllUsersViewModel.getUserListCellFactory());
    }

    public static GroupInfoDialog getInstance(){
        FXMLLoader loader = new FXMLLoader(GroupInfoDialog.class.getResource("group-info-dialog-view.fxml"));
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GroupInfoDialog controller = loader.getController();
        controller.setDialogPane(pane);
        return controller;
    }

    private void makeButtonInvisible() {
        Node closeButton = groupInfoDialog.lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }
}
