package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.ChangingPasswordDialogModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ChangingPasswordDialogTest {
    private static FXMLLoader loader;
    private volatile ChangingPasswordDialog underTest;
    @BeforeAll
    static void beforeAll() {
        try {
            Platform.startup(() -> {});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Platform.runLater(() -> {
            loader = new FXMLLoader(ChangingPasswordDialog.class.getResource("changing-password-dialog-view.fxml"));
            try {
                loader.load();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        ChangingPasswordDialogModel viewModel = new ChangingPasswordDialogModel();
        while (loader.getController() == null) {
            Thread.sleep(200);
        }
        underTest = loader.getController();
        underTest.init(viewModel);
    }

    @Test
    void changeButtonIsDisabledAtStart(){
        //given
        String curP = " ";
        String newP = " ";

        //when
        underTest.getCurrentPassword().setText(curP);
        underTest.getNewPassword().setText(newP);

        //then
        assertThat(underTest.getChangeButton().isDisabled()).isTrue();
    }

}
