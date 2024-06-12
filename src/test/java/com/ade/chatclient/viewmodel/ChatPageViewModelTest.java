package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.model.ClientModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
public class ChatPageViewModelTest {
    private ChatPageViewModel underTest;
    @Mock private ClientModel model;
    @Mock private ViewHandler handler;

    @BeforeEach
    void setUp() {
        underTest = new ChatPageViewModel(handler, model);
    }

    @Test
    void changeButtonsParam() {
        underTest.changeButtonsParam(0);

        assertThat(underTest.getShowUserProfileDisabled().get()).isTrue();
        assertThat(underTest.getShowUsersButtonDisabled().get()).isFalse();
        assertThat(underTest.getShowChatsButtonDisabled().get()).isFalse();
    }

    @Test
    void sendMessage() {
        String message = "text";
        underTest.getMessageTextProperty().set(message);

        underTest.sendMessage();
        verify(model, atLeastOnce()).sendMessageToChat(message, null);
    }

}
