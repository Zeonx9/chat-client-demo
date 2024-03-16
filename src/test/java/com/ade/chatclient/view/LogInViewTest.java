package com.ade.chatclient.view;

import com.ade.chatclient.application.ViewHandler;
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


/**
 * Реализует интеграционный тест между view и viewModel
 * они зависят от модели и ViewHandler, чтобы тестировать в изоляции мы используем
 * mocks для этих двух зависимостей.
 * Цель тестов - проверять, что взаимодействие между view и viewModel происходит правильно и
 * все изменения правильно отражаются во view
 * тесты не запускают реальное приложение, но подключают платформу javafx, из-за чего тесты довольно долго запускаются
 */
@ExtendWith(MockitoExtension.class)
class LogInViewTest {
//    private static FXMLLoader loader;
//    @Mock private ViewHandler handler;
//    @Mock private ClientModel model;
//    private LogInView underTest;
//
//    @BeforeAll
//    static void beforeAll() throws IOException {
//        try {
//            Platform.startup(() -> {});
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        loader = new FXMLLoader(LogInView.class.getResource("log-in-view.fxml"));
//        loader.load();
//    }
//
//    @BeforeEach
//    void setUp() {
//        LogInViewModel viewModel = new LogInViewModel(handler, model);
//        underTest = loader.getController();
//        underTest.init(viewModel);
//    }
//
//    @Test
//    void buttonIsDisabledAtStart(){
//        //given
//        String login = " ";
//        String password = " ";
//
//        //when
//        underTest.getLoginTextField().setText(login);
//        underTest.getPasswordField().setText(password);
//
//        //then
//        assertThat(underTest.getLoginButton().isDisabled()).isTrue();
//    }
//
//    @Test
//    void buttonBecomeEnableIfEnteredText() {
//        //given
//        String login = "login";
//        String password = "password";
//
//        //when
//        underTest.getLoginTextField().setText(login);
//        underTest.getPasswordField().setText(password);
//
//        //then
//        assertThat(underTest.getLoginButton().isDisabled()).isFalse();
//    }
//
//    @Test
//    void buttonClickedAndAuthorizeCalled() {
//        //given
//        String login = "login";
//        String password = "password";
//
//        //when
//        underTest.getLoginTextField().setText(login);
//        underTest.getPasswordField().setText(password);
//        underTest.getLoginButton().fire();
//
//        //then
//        verify(model).authorize(login, password);
//    }
}