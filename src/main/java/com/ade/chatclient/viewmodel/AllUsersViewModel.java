package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractChildViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import lombok.Getter;

import static com.ade.chatclient.application.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.ViewModelUtils.runLaterListener;
import static com.ade.chatclient.application.Views.ALL_CHATS_VIEW;

@Getter
public class AllUsersViewModel extends AbstractChildViewModel<ClientModel> {
    private final ListProperty<User> usersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);
        model.addListener("AllUsers", runLaterListener(listReplacer(usersListProperty)));
    }

    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(false);
    }

    public void onSelectedItemChange(User newValue) {
        if (newValue == null) {
            return;
        }
        Chat created = model.createDialogFromAllUsers(newValue);
        model.setSelectedChat(created);
        viewHandler.openPane(ALL_CHATS_VIEW, placeHolder);
    }

    public static ListCell<User> getUserListCellFactory() {
        return new UserListCellFactory();
    }
}
