package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.ViewModelProvider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * a class that manipulates the views
 * it also starts the first view routine
 * класс, который управляет переключением между разными вью
 * объект этого класса должен существовать только один на все приложение
 * создается в классе StartClientApp требует ссылки на ViewModelProvider
 * для того, чтобы предоставлять ссылки на вью-модели для объектов вью.
 *
 */
public class ViewHandler {

    /**
     * это перечисление определяет все возможные вью в нашем приложении
     * когда создается новое вью, информацию о нем нужно занести в это перечисление
     */
    @AllArgsConstructor
    public enum Views {
        LOG_IN_VIEW("log-in-view"),
        CHAT_PAGE_VIEW("chat-page-view"),
        ALL_USERS_VIEW("all-users-view");
        final String fxmlFileName;
    }

    private final Stage stage;
    private final ViewModelProvider viewModelProvider;

    /**
     * создает новый ViewHandler,
     * при создании инициализирует все вью-модели, передавая им ссылку на ViewHandler
     * @param stage Stage, который предоставляет ClientApplication
     * @param viewModelProvider фабрика вьд-модел, для дальшейшей инициализации вью
     */
    public ViewHandler(Stage stage, ViewModelProvider viewModelProvider) {
        this.viewModelProvider = viewModelProvider;
        this.stage = stage;

        // важная строка, которая инициализирует перед тем, как могут быть созданы вью
        // этот вызов передает в фабрику моделей ссылку на созданный ViewHandler
        // он нужен, чтобы вью-модель могла передать управление другому вью
        viewModelProvider.instantiateViewModels(this);
    }

    /**
     * метод, который запускает самое первое вью, которое должно быть показано пользователю
     */
    public void start() throws IOException {
        openView(Views.LOG_IN_VIEW);
    }

    /**
     * метод, который открывает указанное с помощью константы вью
     * @param viewType константа указывающая на нужное вью
     */
    public void openView(Views viewType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewType.fxmlFileName + ".fxml"));
        Parent root = fxmlLoader.load();

        if (viewType == Views.LOG_IN_VIEW) {
            LogInView view = fxmlLoader.getController();
            view.init(viewModelProvider.getLogInViewModel());
        }

        else if (viewType == Views.CHAT_PAGE_VIEW) {
            ChatPageView view = fxmlLoader.getController();
            view.init(viewModelProvider.getChatPageViewModel());
        }
        else if (viewType == Views.ALL_USERS_VIEW) {
            AllUsersView view = fxmlLoader.getController();
            view.init(viewModelProvider.getAllUsersViewModel());
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        if (viewType == Views.CHAT_PAGE_VIEW || viewType == Views.ALL_USERS_VIEW) {
            stage.setFullScreen(true);
        }
        stage.show();
    }
}
