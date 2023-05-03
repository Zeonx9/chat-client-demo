package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractChildViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter
public class UserProfileViewModel extends AbstractChildViewModel<ClientModel> {
    private final StringProperty fullNameProperty = new SimpleStringProperty();
    private final StringProperty birthDateProperty = new SimpleStringProperty();
    private final StringProperty userNameProperty = new SimpleStringProperty();

    public UserProfileViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(0);
    }

    public void getPersonalUserData() {
        User me = model.getMyself();

        fullNameProperty.set("Дементьев Егор Васильевич");
        birthDateProperty.set("23.06.2003");
        userNameProperty.set(me.getUsername());
    }

}
