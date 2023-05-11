package com.ade.chatclient.view;

import com.ade.chatclient.domain.User;
import com.ade.chatclient.viewmodel.GroupCreationDialogModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GroupCreationDialogTest {
    private static FXMLLoader loader;
    private volatile GroupCreationDialog underTest;
    @BeforeAll
    static void beforeAll() {
        try {
            Platform.startup(() -> {});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Platform.runLater(() -> {
            loader = new FXMLLoader(GroupCreationDialog.class.getResource("create-group-dialog-view.fxml"));
            try {
                loader.load();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        GroupCreationDialogModel viewModel = new GroupCreationDialogModel();
        while (loader.getController() == null) {
            Thread.sleep(200);
        }
        underTest = loader.getController();
        underTest.init(viewModel);
    }

    @Test
    void createGroupButtonIsDisabledAtStart(){
        //given
        User user1 = User.builder().username("user").id(1L).build();
        User user2 = User.builder().username("user").id(2L).build();
        ObservableList<User> userList = FXCollections.observableArrayList(user1, user2);
        ListView<User> users = new ListView<>(userList);
        underTest.setSelectedUsers(users);
        underTest.getCreateGroupButton().fire();

        //when
        underTest.getGroupName().setText("name");
        underTest.setSelectedUsers(users);
        underTest.getCreateGroupButton().fire();

        //then
        assertThat(underTest.getCreateGroupButton().isDisabled()).isFalse();
    }
}
