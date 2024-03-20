package com.ade.chatclient.view;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.viewmodel.AllUsersViewModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class AllUsersViewTest {
    private static FXMLLoader loader;
    @Mock
    private ViewHandler handler;
    @Mock
    private ClientModel model;
    private AllUsersView underTest;

    @BeforeAll
    static void beforeAll() throws IOException {
        Platform.startup(() -> {
        });
        loader = new FXMLLoader(AllUsersView.class.getResource("all-users-view.fxml"));
        loader.load();
    }

    @BeforeEach
    void setUp() {
        AllUsersViewModel viewModel = new AllUsersViewModel(handler, model);
        underTest = loader.getController();
        underTest.init(viewModel);
    }

}



