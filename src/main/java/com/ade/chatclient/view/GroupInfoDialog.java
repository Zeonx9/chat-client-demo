package com.ade.chatclient.view;

import com.ade.chatclient.application.structure.AbstractDialog;
import com.ade.chatclient.application.structure.EmptyDialogModel;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;

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

        Circle circle = new Circle(40, Color.rgb(145, 145, 145));
        Label label = new Label(prepareInitialsToBeShown(chat));
        label.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 20");
        photoPane.getChildren().addAll(circle, label);
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

    private String prepareInitialsToBeShown(Chat chat) {
        String[] chatName = chat.getGroup().getName().split(" ");
        StringBuilder result = new StringBuilder();
        for (String s : chatName) {
            result.append(Character.toUpperCase(s.charAt(0)));
        }
        return result.toString();
    }

    @FXML private void onAddUsersButtonClicked() {systemMessage.setText("This function is not available now");}

    @FXML private void onEditGroupButtonClicked() {systemMessage.setText("This function is not available now");}

    @FXML private void onLeaveGroupButtonClicked() {systemMessage.setText("This function is not available now");}
}
