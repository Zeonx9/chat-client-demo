package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.application.ViewHandler;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * простой тест Junit/Mock, который проверяет правильность работы методов во вью-модели
 * View Model зависит от ViewHandler и от ClientModel, поэтому, чтобы тестировать наш класс в изоляции нужно
 * использовать моки, чтобы создать viewModel для изоляции
 */
@ExtendWith(MockitoExtension.class)
class LogInViewModelTest {
//    private LogInViewModel underTest;
//    @Mock private ClientModel model;
//    @Mock private ViewHandler handler;
//
//    @BeforeEach
//    void setUp() {
//        underTest = new LogInViewModel(handler, model);
//        underTest.setErrorMessageProperty(new SimpleStringProperty());
//        underTest.setLoginTextProperty(new SimpleStringProperty("login"));
//        underTest.setPasswordProperty(new SimpleStringProperty("password"));
//
//    }
//
//    @Test
//    void authorizeIsSuccessful() {
//        //given
//        given(model.authorize("login", "password")).willReturn(true);
//
//        // when
//        underTest.authorize();
//
//        // then
//        assertThat(underTest.getErrorMessageProperty().get()).isEqualTo("");
//    }
//
//    @Test
//    void authorizeIsUnsuccessful() {
//        //given
//        given(model.authorize("login", "password")).willReturn(false);
//
//        // when
//        underTest.authorize();
//
//        // then
//        assertThat(underTest.getErrorMessageProperty().get()).isEqualTo("Unsuccessful");
//    }
//
//    @Test
//    void onTextChangedToNonBlank() {
//        //given
//        String newText = "text";
//
//        // when
//        underTest.onTextChanged(newText);
//
//        // then
//        assertThat(underTest.getDisableButtonProperty().get()).isFalse();
//    }
//
//    @Test
//    void onTextChangedWhenIsBlank() {
//        //given
//        String newText = "    ";
//
//        // when
//        underTest.onTextChanged(newText);
//
//        // then
//        assertThat(underTest.getDisableButtonProperty().get()).isTrue();
//    }
//
//    @Test
//    void onTextChangedWhenIsNotNull() {
//        //given
//        String newText = "login";
//
//        // when
//        Boolean result = underTest.checkChangedText(newText);
//
//        // then
//        assertTrue(result);
//    }
//
//    @Test
//    void onTextChangedWhenIsNull() {
//        //given
//        // when
//        Boolean result = underTest.checkChangedText(null);
//
//        // then
//        assertFalse(result);
//    }

}