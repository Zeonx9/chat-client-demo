package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.application.structure.EmptyDialogModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.components.UserPhoto;
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
public class GroupInfoDialog extends AbstractDialog<GroupChatInfo, EmptyDialogModel<GroupChatInfo>> {
    @FXML private Label systemMessage;
    @FXML private Label countMembers;
    @FXML private Label groupName;
    @FXML private StackPane photoPane;
    @FXML private ListView<User> listMembers;

    /**
     * Вызывает метод инициализации из абстрактного класса, а так же устанавливает все значения в поля интерфейса диалогового окна
     * @param chat - объект класса Chat - беседа, информацию о которой показывает диалоговое окно
     */
    public void setChat(Chat chat) {
        groupName.setText(chat.getGroup().getName());
        countMembers.setText(chat.getMembers().size() + " members");
        systemMessage.setText("");

        listMembers.getItems().setAll(chat.getMembers());
        listMembers.setCellFactory(param -> viewModel.getUserListCellFactory());

        UserPhoto.setPaneContent(photoPane.getChildren(), chat, null, 40, viewModel.getImageRequest());
    }

    public void setImageRequest(Function<String, CompletableFuture<Image>> imageRequest) {
        viewModel.setImageRequest(imageRequest);
    }

    public static GroupInfoDialog getInstance(){
        GroupInfoDialog instance = AbstractDialog.getInstance(GroupInfoDialog.class, "group-info-dialog-view.fxml", "CellFactoryStyle");
        instance.init(new EmptyDialogModel<>());
        return instance;
    }

    @Override
    protected void initialize() {}

    @Override
    protected String getTitleString() {
        return "Group info";
    }

    @FXML private void onAddUsersButtonClicked() {systemMessage.setText("This function is not available now");}

    @FXML private void onEditGroupButtonClicked() {systemMessage.setText("This function is not available now");}

    @FXML private void onLeaveGroupButtonClicked() {systemMessage.setText("This function is not available now");}
}
