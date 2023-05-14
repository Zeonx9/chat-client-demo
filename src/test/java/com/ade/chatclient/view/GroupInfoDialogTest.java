package com.ade.chatclient.view;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GroupInfoDialogTest {
    private static FXMLLoader loader;
    private volatile GroupInfoDialog underTest;
    @BeforeAll
    static void beforeAll() {
        try {
            Platform.startup(() -> {});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Platform.runLater(() -> {
            loader = new FXMLLoader(GroupCreationDialog.class.getResource("group-info-dialog-view.fxml"));
            try {
                loader.load();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        User user3 = User.builder().username("Dasha").id(1L).build();
        Chat chat = Chat.builder().id(1L).isPrivate(false).members(List.of(user1, user2, user3))
                .group(GroupChatInfo.builder().name("group").build()).build();
        while (loader.getController() == null) {
            Thread.sleep(200);
        }
        underTest = loader.getController();
        underTest.setChat(chat);
    }

    @Test
    void getInfo() {
        String groupInfo = "Group info     '" + "group" + "'";
        String membersCount = "3 members";
        User user1 = User.builder().username("Egor").id(2L).build();
        User user2 = User.builder().username("Artem").id(3L).build();
        User user3 = User.builder().username("Dasha").id(1L).build();

        assertThat(underTest.getGroupInfo().getText()).isEqualTo(groupInfo);
        assertThat(underTest.getMembersCount().getText()).isEqualTo(membersCount);
        assertThat(underTest.getListMembers().getItems()).isEqualTo(List.of(user1, user2, user3));
    }

}
