package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.EditChatResult;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.EditChatDialog;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GroupInfoDialogModel extends AbstractDialogModel<GroupChatInfo> {
    @Setter
    @Getter
    private Function<String, CompletableFuture<Image>> imageRequest;
    private final ClientModel model;

    public GroupInfoDialogModel(ClientModel model) {
        super();
        this.model = model;
    }

    @Override
    public GroupChatInfo resultConverter(ButtonType buttonType) {
        return null;
    }

    public ListCell<User> getUserListCellFactory() {
        UserListCellFactory factory = ViewModelUtils.loadCellFactory(
                UserListCellFactory.class,
                "user-list-cell-factory.fxml"
        );
        factory.init(imageRequest);
        return factory;
    }

    public void showEditGroupDialogAndWait() {
        EditChatDialog dialog = EditChatDialog.getInstance(model.getSelectedChat(), model.getMyself());
        dialog.init(new EditChatDialogModel(model::getPhotoById));

        Optional<EditChatResult> answer = dialog.showAndWait();

        answer.ifPresent(result -> {
            model.editGroupName(result.getChangeGroupName().getGroupName());
            //TODO answer.ifPresent(model::тут_функция_для_изменения_фото), ответ я даю вот такой: EditChatResult
        });


    }
}
