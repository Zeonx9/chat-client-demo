package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.EditChatResult;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.EditChatDialogModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class EditChatDialog extends AbstractDialog<EditChatResult, EditChatDialogModel> {
    public StackPane photoPane;
    public TextField firstNameTextField;

    public static EditChatDialog getInstance(Chat curChat, User mySelf) {
        EditChatDialogModel.setCurrentChat(curChat);
        EditChatDialogModel.setMySelfId(mySelf.getId());
        return AbstractDialog.getInstance(EditChatDialog.class, "edit-chat-dialog-view.fxml", "ChatPageViewStyle");
    }

    @Override
    protected void initialize() {
        firstNameTextField.textProperty().bindBidirectional(viewModel.getChatNameProperty());
        Bindings.bindContentBidirectional(photoPane.getChildren(), viewModel.getChatIconNodes());

        viewModel.setChatData();
    }

    @Override
    protected String getTitleString() {
        return "Edit chat information";
    }

    @FXML protected void onChangePhotoClicked() {
        viewModel.openFileChooser();
    }
}
