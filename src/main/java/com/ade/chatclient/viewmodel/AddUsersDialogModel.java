package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import com.ade.chatclient.view.components.UserPhoto;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
@Getter
public class AddUsersDialogModel extends AbstractDialogModel<User> {
    @Setter
    private Function<String, CompletableFuture<Image>> imageRequest;
    private final StringProperty groupName = new SimpleStringProperty();
    private final StringProperty countMembers = new SimpleStringProperty();
    private final ObservableList<Node> photoNodes = FXCollections.observableArrayList();
    private final ListProperty<User> userListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty userClicked = new SimpleBooleanProperty(false);
    private User userToAdd = null;

    @Override
    public User resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }
        return userToAdd;
    }

    public ListCell<User> getUserListCellFactory() {
        UserListCellFactory factory = ViewModelUtils.loadCellFactory(
                UserListCellFactory.class,
                "user-list-cell-factory.fxml"
        );
        factory.init(imageRequest);
        return factory;
    }

    public void onMouseClickedListener(MouseEvent mouseEvent) {
        @SuppressWarnings("unchecked")
        User selectedUser = ((ListView<User>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            return;
        }
        userToAdd = selectedUser;
        userClicked.set(true);
    }

    public void setChat(Chat chat, List<User> allUsers) {
        groupName.set(chat.getGroup().getName());
        countMembers.set(chat.getMembers().size() + " members");

        List<User> usersToAdd = new ArrayList<>();

        for (User allUser : allUsers) {
            boolean add = true;
            for (int j = 0; j < chat.getMembers().size(); j++) {
                if (Objects.equals(allUser.getId(), chat.getMembers().get(j).getId())) {
                    add = false;
                    break;
                }
            }
            if (add) {
                usersToAdd.add(allUser);
            }
        }

        userListProperty.clear();
        userListProperty.setAll(usersToAdd);

        Objects.requireNonNull(UserPhoto.getPaneContent(chat, null, 40, imageRequest))
                .thenAccept(children -> Platform.runLater(() -> {
                    photoNodes.clear();
                    photoNodes.addAll(children);
                }));
    }
}
