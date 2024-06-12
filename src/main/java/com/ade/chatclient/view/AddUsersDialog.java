package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AddUsersDialogModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AddUsersDialog extends AbstractDialog<User, AddUsersDialogModel> {
    @FXML private StackPane photoPane;
    @FXML private Label groupName;
    @FXML private Label countMembers;
    @FXML private ListView<User> listMembers;

    @Override
    protected void initialize() {
        groupName.textProperty().bind(viewModel.getGroupName());
        countMembers.textProperty().bind(viewModel.getCountMembers());

        listMembers.setCellFactory(param -> viewModel.getUserListCellFactory());
        listMembers.itemsProperty().bind(viewModel.getUserListProperty());
        listMembers.setOnMouseClicked(viewModel::onMouseClickedListener);
        viewModel.getUserClicked().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                onOkClicked();
            }
        });

        Bindings.bindContentBidirectional(photoPane.getChildren(), viewModel.getPhotoNodes());
    }

    public void setImageRequest(Function<String, CompletableFuture<Image>> imageRequest) {
        viewModel.setImageRequest(imageRequest);
    }

    public static AddUsersDialog getInstance(){
        return AbstractDialog.getInstance(AddUsersDialog.class, "add-users-dialog-view.fxml", "CellFactoryStyle");
    }

    public void setChat(Chat chat, List<User> users) {
        viewModel.setChat(chat, users);
    }

    @Override
    protected String getTitleString() {
        return "Add user to chat";
    }
}
