package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.*;
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

import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
import static com.ade.chatclient.application.Views.LOG_IN_VIEW;

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

    private void showRequestResult(PropertyChangeEvent event) {
        resultMessageProperty.set((String) event.getNewValue());
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(0);
    }

    public void setUserPersonalData() {
        User me = model.getMyself();

        fullNameProperty.set(prepareFullName(me));
        birthDateProperty.set(formatDOB(me.getDateOfBirth()));
        userNameProperty.set(me.getUsername());
        companyNameProperty.set(model.getCompany().getName());
    }

    public void showDialogAndWait() {
        resultMessageProperty.set("");
        ChangingPasswordDialog dialog = ChangingPasswordDialog.getInstance();
        dialog.init(new ChangingPasswordDialogModel());

        Optional<ChangePasswordRequest> answer = dialog.showAndWait();
        answer.ifPresent(model::changePassword);
    }

    private String formatDOB(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

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

    public void logOut() {
        model.clearModel();
        StartClientApp.stop();
        viewHandler.openView(LOG_IN_VIEW);
    }
}
