package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.application.structure.EmptyDialogModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс выступает в роли контроллера для диалогового окна с информацией о беседе, управляет поведением и отображением элементов на экране
 */
@Setter
@Getter
public class GroupInfoDialog extends AbstractDialog<GroupChatInfo, EmptyDialogModel<GroupChatInfo>> {
    @FXML private Label groupInfo;
    @FXML private Label membersCount;
    @FXML private ListView<User> listMembers;

    /**
     * Вызывает метод инициализации из абстрактного класса, а так же устанавливает все значения в поля интерфейса диалогового окна
     * @param chat - объект класса Chat - беседа, информацию о которой показывает диалоговое окно
     */
    public void setChat(Chat chat) {
        groupInfo.setText("Group info     '" + chat.getChatName(null) + "'");
        membersCount.setText(chat.getMembers().size() + " members");
        listMembers.getItems().setAll(chat.getMembers());
        listMembers.setCellFactory(param -> AllUsersViewModel.getUserListCellFactory());
    }

    public static GroupInfoDialog getInstance(){
        GroupInfoDialog instance = AbstractDialog.getInstance(GroupInfoDialog.class, "group-info-dialog-view.fxml");
        instance.init(new EmptyDialogModel<>());
        return instance;
    }

    @Override
    protected void initialize() {}

    @Override
    protected String getTitleString() {
        return "Group info";
    }
}
