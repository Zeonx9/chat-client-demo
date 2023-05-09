package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class BackgroundServiceTest {
    private BackgroundService underTest;
    private ClientModel model;

    @BeforeEach
    void setUp() {
        model = mock(ClientModel.class);
        underTest = new BackgroundService(model);
    }

    @Test
    void run() {
        // when
        underTest.run();

        //verify
        verify(model, times(1)).fetchUsers();
        verify(model, times(1)).fetchChats();
        verify(model, atLeastOnce()).fetchNewMessages();

        underTest.stop();
    }
}
