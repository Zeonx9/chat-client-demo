package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractChildViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ChangingPasswordDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
public class UserProfileViewModel extends AbstractChildViewModel<ClientModel> {
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    private final StringProperty birthDateProperty = new SimpleStringProperty();
    private final StringProperty userNameProperty = new SimpleStringProperty();
    private final StringProperty companyNameProperty = new SimpleStringProperty();

    public UserProfileViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(0);
    }

    public void getPersonalUserData() {
        User me = model.getMyself();

        fullNameProperty.set((me.getRealName() == null && me.getSurname() == null) ? "-" :
                ((me.getRealName() == null) ? "" : me.getRealName() + " ") +
                        ((me.getSurname() == null) ? "" : me.getSurname()));

        birthDateProperty.set((me.getDateOfBirth() == null) ? "-" :
                me.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        userNameProperty.set(me.getUsername());
        companyNameProperty.set(model.getCompany().getName());
    }
    public void showDialogAndWait() {
        ChangingPasswordDialog dialog = ChangingPasswordDialog.getInstance();
        dialog.init(model.getMyself(), new ChangingPasswordDialogModel());

        Optional<ChangePasswordRequest> answer = dialog.showAndWait();
        if (answer.isPresent()) {
            System.out.println(answer.get());
            model.changePassword(answer.get());
        }
    }

}
