package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.model.ClientModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.application.Platform;


import javafx.scene.input.MouseEvent;
import java.io.File;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AllChatsViewModelTest {

    private AllChatsViewModel underTest;
    @Mock
    private ClientModel model;
    @Mock private ViewHandler handler;


    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        underTest = new AllChatsViewModel(handler, model);
        String mediaPath = "src/main/resources/com/ade/chatclient/sounds/sound.mp3";
        underTest.setMediaPlayer(new MediaPlayer(new Media(new File(mediaPath).toURI().toString())));
    }

    @Test
    void onTextChangedNull() {

        String newText = "    ";

        underTest.onTextChanged(newText);

        assertThat(underTest.getIsSearching()).isFalse();
    }

    @Test
    void onTextChangedTrue() {

        String newText = "text";

        underTest.onTextChanged(newText);

        assertThat(underTest.getIsSearching()).isTrue();
        verify(model).searchChat(newText);
    }

    @Test
    void onMouseClickedListener() {
        Chat chat1 = Chat.builder().id(1L).build();
        Chat chat2 = Chat.builder().id(2L).build();
        ObservableList<Chat> chatList = FXCollections.observableArrayList(chat1, chat2);
        ListView<Chat> listView = new ListView<>(chatList);
        listView.getSelectionModel().select(chat2);
        MouseEvent mouseEvent = mock(MouseEvent.class);
        given(mouseEvent.getSource()).willReturn(listView);

        underTest.onMouseClickedListener(mouseEvent);

        verify(model, times(1)).selectChat(chat2);
        assertEquals(chat2, underTest.getSelected());
    }

}



