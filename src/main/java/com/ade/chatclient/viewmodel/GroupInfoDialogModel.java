package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.EditChatResult;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.AddUsersDialog;
import com.ade.chatclient.view.EditChatDialog;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;

@Getter
public class GroupInfoDialogModel extends AbstractDialogModel<GroupChatInfo> {
    @Setter
    private Function<String, CompletableFuture<Image>> imageRequest;
    private final StringProperty groupName = new SimpleStringProperty();
    private final StringProperty countMembers = new SimpleStringProperty();
    private final StringProperty systemMessage = new SimpleStringProperty();
    private final ListProperty<User> userListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObservableList<Node> photoNodes = FXCollections.observableArrayList();
    private final ClientModel model;
    private User creator = null;

    public static final String NEW_GROUP_CHAT_INFO = "newGroupChatInfo";

    public GroupInfoDialogModel(ClientModel model) {
        super();
        this.model = model;
        model.addListener(NEW_GROUP_CHAT_INFO, runLaterListener(this::addUserToChatListener));
    }

    private void addUserToChatListener(PropertyChangeEvent event) {
        @SuppressWarnings("unchecked")
        Chat newChat = (Chat) event.getNewValue();
        setChat(newChat);
    }

    @Override
    public GroupChatInfo resultConverter(ButtonType buttonType) {
        return null;
    }

    public void setChat(Chat chat) {
        groupName.set(chat.getGroup().getName());
        countMembers.set(chat.getMembers().size() + " members");
        systemMessage.set("");
        creator = chat.getGroup().getCreator();

        userListProperty.clear();
        userListProperty.setAll(chat.getMembers());

        Objects.requireNonNull(UserPhoto.getPaneContent(chat, null, 40, imageRequest))
                .thenAccept(children -> Platform.runLater(() -> {
                    photoNodes.clear();
                    photoNodes.addAll(children);
                }));
    }

    public ListCell<User> getUserListCellFactory() {
        UserListCellFactory factory = ViewModelUtils.loadCellFactory(
                UserListCellFactory.class,
                "user-list-cell-factory.fxml"
        );
        factory.init(imageRequest);
        if (Objects.equals(creator.getId(), model.getMyself().getId())) {
            factory.addOnDeleteListener(userId -> {
                System.out.println("DELETE: clicked user with id = " + userId);
                model.removeUserFromGroupChat(userId);
            });
        }
        return factory;
    }

    public void showEditGroupDialogAndWait() {
        EditChatDialog dialog = EditChatDialog.getInstance(model.getSelectedChat(), model.getMyself());
        dialog.init(new EditChatDialogModel(model::getPhotoById));

        Optional<EditChatResult> answer = dialog.showAndWait();

        answer.ifPresent(result -> {
            if (result.getChangeGroupName() != null) model.editGroupName(result.getChangeGroupName().getGroupName());
            if (result.getFile() != null) model.uploadGroupChatPhoto(result.getFile());
        });
    }

    public void leaveGroup() {
        model.removeUserFromGroupChat(model.getMyself().getId());
    }

    public void showAddUserToChatDialogAndWait() {
        AddUsersDialog dialog = AddUsersDialog.getInstance();
        dialog.init(new AddUsersDialogModel());
        dialog.setImageRequest(model::getPhotoById);
        dialog.setChat(model.getSelectedChat(), model.getAllUsers());

        Optional<User> answer = dialog.showAndWait();
        answer.ifPresent(user -> {
            model.addUserToGroupChat(user.getId());
            System.out.println("Add user with id:" + user.getId());
        });
    }
}
