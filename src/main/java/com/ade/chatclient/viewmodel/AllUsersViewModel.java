package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.util.ViewModelUtils;
import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.cellfactory.UserListCellFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import lombok.Getter;

import static com.ade.chatclient.application.util.ViewModelUtils.listReplacer;
import static com.ade.chatclient.application.util.ViewModelUtils.runLaterListener;
import static com.ade.chatclient.application.Views.ALL_CHATS_VIEW;

/**
 * Класс, который связывает model с AllUsersView.
 * Регистрирует лисенер - "AllUsers"
 */
@Getter
public class AllUsersViewModel extends AbstractChildViewModel<ClientModel> {
    private final ListProperty<User> usersListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public static final String ALL_USERS_EVENT = "AllUsers";
    public AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);
        //заменяет значение usersListProperty новым значением (лист юзеров)
        model.addListener(ALL_USERS_EVENT, runLaterListener(listReplacer(usersListProperty)));
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

    /**
     * @return фабрику ячеек для юзеров
     */
    public ListCell<User> getUserListCellFactory() {
        return ViewModelUtils.loadCellFactory(
                UserListCellFactory.class,
                "user-list-cell-factory.fxml"
        );
    }

    /**
     * При изменении текста newText в поле для поиска вызывает
     * метод model для поиска пользователей, если текст не пустой, иначе завершает поиск
     * @param newText измененный текст
     */
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
