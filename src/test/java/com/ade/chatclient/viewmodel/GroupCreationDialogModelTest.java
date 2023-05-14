package com.ade.chatclient.viewmodel;

import com.ade.chatclient.domain.GroupChatInfo;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.GroupRequest;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GroupCreationDialogModelTest {
    private GroupCreationDialogModel underTest;
    @BeforeEach
    void setUp() {
        underTest = new GroupCreationDialogModel();
    }

    @Test
    void populateUserList() {
        //given
        User user = User.builder().username("user").id(1L).build();
        List<User> users = new ArrayList<>(List.of(user));

        //when
        underTest.populateUserList(users);

        //then
        assertThat(underTest.getUserListProperty().stream().toList()).isEqualTo(users);

    }

    @Test
    void onNewMemberSelected() {
        //given
        User user = User.builder().username("user").id(1L).build();
        List<User> users = new ArrayList<>(List.of(user));

        //when
        underTest.onNewMemberSelected(user);

        //then
        assertThat(underTest.getSelectedUsersListProperty().stream().toList()).isEqualTo(users);

    }

    @Test
    void onAlreadySelectedClickListener() {
        //given
        User user = User.builder().username("user").id(1L).build();

        //when
        underTest.onAlreadySelectedClickListener(user);

        //then
        assertThat(underTest.getSelected()).isEqualTo(user);
    }

    @Test
    void onMouseClickedListener() {
        //given
        User user = User.builder().username("user").id(1L).build();
        List<User> users = new ArrayList<>(List.of(user));
        underTest.getSelectedUsersListProperty().addAll(users);
        underTest.setSelected(user);
        //when
        underTest.onMouseClickedListener(null);

        //then
        assertThat(underTest.getSelectedUsersListProperty().isEmpty()).isTrue();
    }

    @Test
    void onTextChanged() {
        //given
        String newText = "text";
        // when
        underTest.onTextChanged(newText);
        // then
        assertThat(underTest.getIsFilled().get()).isFalse();
    }

    @Test
    void  resultConverter() {
        underTest.getNameOfGroup().set("name");
        User user1 = User.builder().username("user").id(1L).build();
        User user2 = User.builder().username("user").id(2L).build();
        List<User> users = new ArrayList<>(List.of(user1, user2));
        underTest.getSelectedUsersListProperty().addAll(users);

        GroupChatInfo info = GroupChatInfo.builder().name("name").build();
        GroupRequest request = GroupRequest.builder().groupInfo(info).build();
        request.getIds().add(1L);
        request.getIds().add(2L);

        GroupRequest result = underTest.resultConverter(ButtonType.OK);

        assertEquals(result, request);
    }


}
