package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.application.ViewHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * простой тест Junit/Mock, который проверяет правильность работы методов во вью-модели
 * Вью модель зависит от ViewHandler и от ClientModel, поэтому, чтобы тестировать наш класс в изоляции нужно
 * использовать моки, чтобы создать вьюмодельку для изоляции
 */
@ExtendWith(MockitoExtension.class)
class LogInViewModelTest {
    private LogInViewModel underTest;
    @Mock private ClientModel model;
    @Mock private ViewHandler handler;

    @BeforeEach
    void setUp() {
        underTest = new LogInViewModel(handler, model);
    }

    @Test
    void authorize() {

    }

    @Test
    void onTextChangedToNonBlank() {
        //given
        String newText = "text";
        // when
        underTest.onTextChanged(newText);
        // then
        assertThat(underTest.getDisableButtonProperty().get()).isFalse();
    }

    @Test
    void onTextChangedWhenIsBlank() {
        //given
        String newText = "    ";
        // when
        underTest.onTextChanged(newText);
        // then
        assertThat(underTest.getDisableButtonProperty().get()).isTrue();
    }
}