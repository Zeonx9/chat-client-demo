package com.ade.chatclient.view;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.viewmodel.AllChatsViewModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;


@ExtendWith(MockitoExtension.class)
public class AllChatsViewTest {
    private static FXMLLoader loader;
    @Mock
    private ViewHandler handler;
    @Mock
    private ClientModel model;
    private AllChatsView underTest;

    @BeforeAll
    static void beforeAll() throws IOException {
        try {
            Platform.startup(() -> {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        loader = new FXMLLoader(AllChatsView.class.getResource("all-chats-view.fxml"));
        loader.load();
    }

    @BeforeEach
    void setUp() {
        AllChatsViewModel viewModel = new AllChatsViewModel(handler, model);
        underTest = loader.getController();
        underTest.init(viewModel);
    }
}
