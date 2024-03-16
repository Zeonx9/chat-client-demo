package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.*;
import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.application.util.PaneSwitcher;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ChangingPasswordDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.util.Optional;

import static com.ade.chatclient.application.Views.PROFILE_VIEW;
import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;
import static com.ade.chatclient.application.Views.LOG_IN_VIEW;

/**
 * Класс, который связывает model с UserProfileView
 * Регистрирует лисенер - "passwordChangeResponded"
 */
@Getter
public class UserSettingsViewModel extends AbstractChildViewModel<ClientModel> {
    private final StringProperty systemMessageProperty = new SimpleStringProperty();
    private PaneSwitcher paneSwitcher;

    public static final String PASSWORD_CHANGED_RESPONDED_EVENT = "passwordChangeResponded";
    public UserSettingsViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener(PASSWORD_CHANGED_RESPONDED_EVENT, runLaterListener(this::showRequestResult));
    }

    /**
     * Устанавливает сообщение о результате смены пароля
     *
     * @param event строка с результатом изменения пароля
     */
    private void showRequestResult(PropertyChangeEvent event) {
        systemMessageProperty.set((String) event.getNewValue());
    }

    /**
     * Метод вызывается при переключении на UserProfileView, обновляет состояние боковых кнопок
     */
    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(0);
    }

    /**
     * Метод создает и инициализирует диалоговое окно для смены пароля, отображает его на экране, после чего собирает данные от пользователя и отправляет их в модель для смены пароля
     */
    public void showDialogAndWait() {
        systemMessageProperty.set("");
        ChangingPasswordDialog dialog = ChangingPasswordDialog.getInstance();
        dialog.init(new ChangingPasswordDialogModel());

        Optional<ChangePasswordRequest> answer = dialog.showAndWait();
        answer.ifPresent(model::changePassword);
    }

    /**
     * Метод запускает процесс выхода из аккаунта, очищает данные о пользователе в модели, останавливает работу BackgroundService и открывает окно входа в аккаунт
     */
    public void logOut() {
        model.clearModel();
        StartClientApp.stop();
        viewHandler.openView(LOG_IN_VIEW);
    }

    public void openUserProfile(Pane placeHolder) {
        viewHandler.getViewModelProvider().getProfileViewModel().setUser(model.getMyself());

        paneSwitcher = new PaneSwitcher(viewHandler, placeHolder);
        paneSwitcher.switchTo(PROFILE_VIEW);
    }

    public void showEditProfileDialogAndWait() {
        systemMessageProperty.set("This function is not available now");
    }
}
