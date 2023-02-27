package com.ade.chatclient.view;

import com.ade.chatclient.viewmodel.BackgroundService;
import com.ade.chatclient.viewmodel.ViewModelProvider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.function.BiConsumer;

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
        LOG_IN_VIEW(
                "log-in-view",
                (loader, vmProvider) -> {
                    LogInView view = loader.getController();
                    view.init(vmProvider.getLogInViewModel());
                }
        ),
        CHAT_PAGE_VIEW(
                "chat-page-view",
                (loader, vmProvider) -> {
                    ChatPageView view = loader.getController();
                    view.init(vmProvider.getChatPageViewModel());
                }
        ),
        ALL_USERS_VIEW(
                "all-users-view",
                (loader, vmProvider) -> {
                    AllUsersView view = loader.getController();
                    view.init(vmProvider.getAllUsersViewModel());
                }
        );
        final String fxmlFileName;
        final BiConsumer<FXMLLoader, ViewModelProvider> viewInitializer;
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
    public void start() {
        openView(Views.LOG_IN_VIEW);
    }

    /**
     * запускает потоки, которые будут работать в фоне и проверять
     * наличие новых сообщений или чатов
     */
    public void startBackGroundServices() {
        BackgroundService backgroundService = viewModelProvider.getBackgroundService();
        backgroundService.run();
    }

    /**
     * метод, который открывает указанное с помощью константы вью
     * @param viewType константа указывающая на нужное вью
     */
    public void openView(Views viewType) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewType.fxmlFileName + ".fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("cannot open the view: " + viewType.fxmlFileName);
            throw new RuntimeException(e);
        }

        viewType.viewInitializer.accept(fxmlLoader, viewModelProvider);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchPane(Views paneType, Pane placeHolder) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(paneType.fxmlFileName + ".fxml"));
        Parent paneRoot;
        try {
            paneRoot = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("cannot switch to pane: " + paneType.fxmlFileName);
            throw new RuntimeException(e);
        }

        //init the controller Of Pane
        paneType.viewInitializer.accept(fxmlLoader, viewModelProvider);
        placeHolder.getChildren().setAll(paneRoot);
    }
}
