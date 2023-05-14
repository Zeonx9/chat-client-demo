package com.ade.chatclient.viewmodel;

import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChangingPasswordDialogModelTest {
    private ChangingPasswordDialogModel underTest;
    @BeforeEach
    void setUp() {
        underTest = new ChangingPasswordDialogModel();
    }

    @Test
    void onCurPasswordTextChangedNull() {
        //given
        // when
        underTest.onCurPasswordTextChanged(null);
        // then
        assertThat(underTest.getIsCurPasswordBlank().get()).isTrue();

    }

    @Test
    void onCurPasswordTextChangedIsNotNull() {
        //given
        String newText = "text";
        // when
        underTest.onCurPasswordTextChanged(newText);
        // then
        assertThat(underTest.getIsCurPasswordBlank().get()).isFalse();

    }

    @Test
    void onNewPasswordTextChanged() {
        //given
        // when
        underTest.onNewPasswordTextChanged(null);
        // then
        assertThat(underTest.getIsNewPasswordBlank().get()).isTrue();

    }

    @Test
    void resultConverterOK() {
        //given
        ButtonType buttonType = ButtonType.OK;
        underTest.getCurPassword().set("oldPassword");
        underTest.getNewPassword().set("newPassword");
        AuthRequest info = AuthRequest.builder().password(underTest.getCurPassword().getValue()).build();
        ChangePasswordRequest request = ChangePasswordRequest.builder().authRequest(info).newPassword(underTest.getNewPassword().getValue()).build();
        // when
        ChangePasswordRequest result = underTest.resultConverter(buttonType);
        // then
        assertEquals(result, request);

    }

    @Test
    void resultConverterCLOSE() {
        //given
        ButtonType buttonType = ButtonType.CLOSE;
        // when
        ChangePasswordRequest result = underTest.resultConverter(buttonType);
        // then
        assertNull(result);
    }

    @Test
    void onChangeClicked() {
        //given
        String oldP = "oldPassword";
        String newP = "newPassword";
        underTest.getCurPassword().set(oldP);
        underTest.getNewPassword().set(newP);

        AuthRequest info = AuthRequest.builder().password(oldP).build();
        ChangePasswordRequest request = ChangePasswordRequest.builder().authRequest(info).newPassword(newP).build();

        // when
        ChangePasswordRequest result = underTest.onOkClicked();
        // then
        assertEquals(result, request);
    }


}
