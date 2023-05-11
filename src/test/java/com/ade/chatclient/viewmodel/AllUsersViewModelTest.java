package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;

import static com.ade.chatclient.application.Views.ALL_CHATS_VIEW;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AllUsersViewModelTest {
    private AllUsersViewModel underTest;
    @Mock
    private ClientModel model;
    @Mock private ViewHandler handler;
    Pane placeHolder;

    @BeforeEach
    void setUp() {
        underTest = new AllUsersViewModel(handler, model);
    }

    @Test
    void onTextChangedIsEmpty() {
        //given
        String newText = "    ";
        List<User> allUsers = new ArrayList<>();
        allUsers.add(User.builder().id(1L).build());

        // when
        given(model.getAllUsers()).willReturn(allUsers);
        underTest.onTextChanged(newText);

        // then
        assertThat(underTest.getUsersListProperty()).isEqualTo(allUsers);
    }

    @Test
    void onTextChangedIsNotEmpty() {
        //given
        String newText = "login";
        List<User> searchingUsers = new ArrayList<>();
        searchingUsers.add(User.builder().id(1L).username("login").build());

        // when
        given(model.searchUser(newText)).willReturn(searchingUsers);
        underTest.onTextChanged(newText);

        // then
        assertThat(underTest.getUsersListProperty()).isEqualTo(searchingUsers);
    }

    @Test
    void onSelectedItemChange() {
        //given
        User newValue = User.builder().id(1L).build();
        Chat chat = Chat.builder().id(1L).build();

        // when
        given(model.createDialogFromAllUsers(newValue)).willReturn(chat);

        // then
        underTest.onSelectedItemChange(newValue);
        verify(model).setSelectChat(chat);
        verify(handler).openPane(ALL_CHATS_VIEW, placeHolder);
    }

}
