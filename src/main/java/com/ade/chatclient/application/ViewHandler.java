package com.ade.chatclient.application;

import com.ade.chatclient.view.LogInView;
import com.ade.chatclient.viewmodel.BackgroundService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;

import static com.ade.chatclient.application.Views.LOG_IN_VIEW;

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
    private final Stage stage;
    @Getter
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
        viewModelProvider.instantiateViewModels(this);
    }

    /**
     * метод, который запускает самое первое вью, которое должно быть показано пользователю
     */
    public void start() {
        openView(LOG_IN_VIEW);
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
        FXMLLoader fxmlLoader = new FXMLLoader(LogInView.class.getResource(viewType.fxmlFileName + ".fxml"));
        Parent root = load(fxmlLoader);

        AbstractView<AbstractViewModel<?>> view = fxmlLoader.getController();
        view.init(viewType.viewModelGetter.apply(viewModelProvider));

        // создать сцену для нового вью и установить его на stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * открывает заданную панель в заданном контейнере
     * @param paneType - тип панели для открытия
     * @param placeHolder - контейнер типа Pane или его наследник, на месте которого будет открыта новая панель
     */
    public void openPane(Views paneType, Pane placeHolder) {
        FXMLLoader fxmlLoader = new FXMLLoader(LogInView.class.getResource(paneType.fxmlFileName + ".fxml"));
        Parent paneRoot = load(fxmlLoader);

        AbstractView<AbstractChildViewModel<?>> pane = fxmlLoader.getController();
        pane.init((AbstractChildViewModel<?>) paneType.viewModelGetter.apply(viewModelProvider));
        pane.getViewModel().setPlaceHolder(placeHolder);
        pane.getViewModel().actionInParentOnOpen();

        // эта строчка устанавливает уже инициализированную панель на ее место в родителе
        placeHolder.getChildren().setAll(paneRoot);
        if (placeHolder instanceof AnchorPane) {
            anchorPaneInParent(paneRoot);
        }
    }

    private static Parent load(FXMLLoader fxmlLoader) {
        try {
            return fxmlLoader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void anchorPaneInParent(Node child) {
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
    }
}
