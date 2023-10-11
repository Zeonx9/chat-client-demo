package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.*;
import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ChangingPasswordDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;
import static com.ade.chatclient.application.Views.LOG_IN_VIEW;

/**
 * Класс, который связывает model с UserProfileView
 * Регистрирует лисенер - "passwordChangeResponded"
 */
@Getter
public class UserProfileViewModel extends AbstractChildViewModel<ClientModel> {
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    private final StringProperty birthDateProperty = new SimpleStringProperty();
    private final StringProperty userNameProperty = new SimpleStringProperty();
    private final StringProperty companyNameProperty = new SimpleStringProperty();
    private final StringProperty resultMessageProperty = new SimpleStringProperty();

    public UserProfileViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);

        model.addListener("passwordChangeResponded", runLaterListener(this::showRequestResult));
    }

    /**
     * Устанавливает сообщение о результате смены пароля
     * @param event строка с результатом изменения пароля
     */
    private void showRequestResult(PropertyChangeEvent event) {
        resultMessageProperty.set((String) event.getNewValue());
    }

    /**
     * Метод вызывается при переключении на UserProfileView, обновляет состояние боковых кнопок
     */
    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(0);
    }

    /**
     * Метод получает из модели объект класса User пользователя и устанавливает личную информацию на экран
     */
    public void setUserPersonalData() {
        User me = model.getMyself();

        fullNameProperty.set(prepareFullName(me));
        birthDateProperty.set(formatDOB(me.getDateOfBirth()));
        userNameProperty.set(me.getUsername());
        companyNameProperty.set(model.getCompany().getName());
    }

    /**
     * Метод создает и инициализирует диалоговое окно для смены пароля, отображает его на экране, после чего собирает данные от пользователя и отправляет их в модель для смены пароля
     */
    public void showDialogAndWait() {
        resultMessageProperty.set("");
        ChangingPasswordDialog dialog = ChangingPasswordDialog.getInstance();
        dialog.init(new ChangingPasswordDialogModel());

        Optional<ChangePasswordRequest> answer = dialog.showAndWait();
        answer.ifPresent(model::changePassword);
    }

    /**
     * @param date дата рождения пользователя
     * @return дату рождения в формате "dd.MM.yyyy"
     */
    private String formatDOB(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * @param user объект класса User - пользователь
     * @return имя и фамилию пользователя, если она указана
     */
    private String prepareFullName(User user) {
        String raw = thisOrEmpty(user.getRealName()) + " " + thisOrEmpty(user.getSurname());
        if (raw.isBlank()) {
            return "-";
        }
        return raw.trim();
    }

    private String thisOrEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * Метод запускает процесс выхода из аккаунта, очищает данные о пользователе в модели, останавливает работу BackgroundService и открывает окно входа в аккаунт
     */
    public void logOut() {
        model.clearModel();
        StartClientApp.stop();
        viewHandler.openView(LOG_IN_VIEW);
    }
}
