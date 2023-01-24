package com.ade.chatclient.view;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.viewmodel.LogInViewModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class LogInViewTest {
    private static FXMLLoader loader;

    @Mock private ViewHandler handler;
    @Mock private ClientModel model;

    private LogInView underTest;

    @BeforeAll
    static void beforeAll() throws IOException {
        Platform.startup(() -> {});
        loader = new FXMLLoader(LogInView.class.getResource("log-in-view.fxml"));
        loader.load();
    }

    @BeforeEach
    void setUp() {
        LogInViewModel viewModel = new LogInViewModel(handler, model);
        underTest = loader.getController();
        underTest.init(viewModel);
    }

    @Test
    void buttonIsDisabledAtStart() {
        assertThat(underTest.getLoginButton().isDisabled()).isTrue();
    }

    @Test
    void buttonBecomeEnableIfEnteredText() {
        //given
        String text = "text";

        //when
        underTest.getLoginTextField().setText(text);

        //then
        assertThat(underTest.getLoginButton().isDisabled()).isFalse();
    }

    @Test
    void buttonClickedAndAuthorizeCalled() {
        //given
        String text = "text";

        //when
        underTest.getLoginTextField().setText(text);
        underTest.getLoginButton().fire();

        //then
        verify(model).Authorize(text);

    }
}