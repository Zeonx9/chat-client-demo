package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.GroupInfoDialogModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Класс выступает в роли контроллера для диалогового окна с информацией о беседе, управляет поведением и отображением элементов на экране
 */
@Setter
@Getter
public class GroupInfoDialog extends AbstractDialog<GroupChatInfo, GroupInfoDialogModel> {
    @FXML private Label systemMessage;
    @FXML private Label countMembers;
    @FXML private Label groupName;
    @FXML private StackPane photoPane;
    @FXML private ListView<User> listMembers;

    @Override
    protected void initialize() {
        groupName.textProperty().bind(viewModel.getGroupName());
        systemMessage.textProperty().bind(viewModel.getSystemMessage());
        countMembers.textProperty().bind(viewModel.getCountMembers());

        listMembers.setCellFactory(param -> viewModel.getUserListCellFactory());
        listMembers.itemsProperty().bind(viewModel.getUserListProperty());

        Bindings.bindContentBidirectional(photoPane.getChildren(), viewModel.getPhotoNodes());
    }

    /**
     * Вызывает метод инициализации из абстрактного класса, а так же устанавливает все значения в поля интерфейса диалогового окна
     * @param chat - объект класса Chat - беседа, информацию о которой показывает диалоговое окно
     */
    public void setChat(Chat chat) {
        viewModel.setChat(chat);
    }

    public void setImageRequest(Function<String, CompletableFuture<Image>> imageRequest) {
        viewModel.setImageRequest(imageRequest);
    }

    public static GroupInfoDialog getInstance(){
        return AbstractDialog.getInstance(GroupInfoDialog.class, "group-info-dialog-view.fxml", "CellFactoryStyle");
    }

    @Override
    protected String getTitleString() {
        return "Group info";
    }

    @FXML private void onAddUsersButtonClicked() {viewModel.showAddUserToChatDialogAndWait();}

    @FXML private void onEditGroupButtonClicked() {viewModel.showEditGroupDialogAndWait();}

    @FXML private void onLeaveGroupButtonClicked() {viewModel.leaveGroup();}
}
