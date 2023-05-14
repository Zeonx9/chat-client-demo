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

    /**
     * Метод вызывается при переключении на AllUsersView, обновляет состояние боковых кнопок и список пользователей
     */
    @Override
    public void actionInParentOnOpen() {
        viewHandler.getViewModelProvider().getChatPageViewModel().changeButtonsParam(1);
        model.getAllUsersAfterSearching();
    }

    /**
     * Метод срабатывает при выборе пользователя, переключает на окно с чатами и открывает диалог с данным человеком
     * @param newValue объект класса User - выбранный пользователь
     */
    public void onSelectedItemChange(User newValue) {
        if (newValue == null) {
            return;
        }
        Chat created = model.createDialogFromAllUsers(newValue);
        model.setSelectChat(created);
        viewHandler.openPane(ALL_CHATS_VIEW, placeHolder);
    }

    public static ListCell<User> getUserListCellFactory() {
        return new UserListCellFactory();
    }

    public void onTextChanged(String newText) {
        usersListProperty.clear();

        if (newText == null || newText.isBlank()) {
            usersListProperty.clear();
            usersListProperty.addAll(model.getAllUsers());
            return;
        }
        usersListProperty.addAll(model.searchUser(newText));
    }
}
