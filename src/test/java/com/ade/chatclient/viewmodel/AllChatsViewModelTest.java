package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.AllChatsView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.io.IOException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AllChatsViewModelTest {
    private AllChatsViewModel underTest;
    @Mock private ClientModel model;
    @Mock private ViewHandler handler;
    private static FXMLLoader loader;

    @BeforeAll
    static void initJfxRuntime() throws IOException {
        try {
            Platform.startup(() -> {});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        loader = new FXMLLoader(AllChatsView.class.getResource("all-chats-view.fxml"));
        loader.load();
    }

    @BeforeEach
    void setUp() {
        underTest = new AllChatsViewModel(handler, model);
        AllChatsView allChatsView = loader.getController();
        allChatsView.init(underTest);

        String mediaPath = "src/main/resources/com/ade/chatclient/sounds/sound.mp3";
//        underTest.setMediaPlayer(new MediaPlayer(new Media(new File(mediaPath).toURI().toString())));
    }

    @Test
    void onTextChangedNull() {
        //given
        String newText = "    ";

        // when
        underTest.onTextChanged(newText);

        // then
        assertThat(underTest.getIsSearching()).isFalse();
    }

    @Test
    void onTextChangedTrue() {
        //given
        String newText = "text";

        // when
        underTest.onTextChanged(newText);

        // then
        assertThat(underTest.getIsSearching()).isTrue();
        verify(model).searchChat(newText);
    }

    @Test
    void onMouseClickedListener() {
        //given
        Chat chat1 = Chat.builder().id(1L).build();
        Chat chat2 = Chat.builder().id(2L).build();
        ObservableList<Chat> chatList = FXCollections.observableArrayList(chat1, chat2);
        ListView<Chat> listView = new ListView<>(chatList);
        listView.getSelectionModel().select(chat2);
        MouseEvent mouseEvent = mock(MouseEvent.class);
        given(mouseEvent.getSource()).willReturn(listView);

        // when
        underTest.onMouseClickedListener(mouseEvent);

        // then
        verify(model, times(1)).setSelectChat(chat2);
        assertEquals(chat2, underTest.getSelected());
    }

}



