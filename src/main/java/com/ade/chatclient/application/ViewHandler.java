package com.ade.chatclient.application;

import com.ade.chatclient.ClientApplication;
import com.ade.chatclient.application.structure.AbstractChildViewModel;
import com.ade.chatclient.application.structure.AbstractView;
import com.ade.chatclient.application.structure.AbstractViewModel;
import com.ade.chatclient.view.LogInView;
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
     * Создает новый ViewHandler,
     * при создании инициализирует все вью-модели, передавая им ссылку на ViewHandler
     * @param stage Stage, который предоставляет ClientApplication
     * @param viewModelProvider фабрика вьд-модель, для дальнейшей инициализации вью
     */
    public ViewHandler(Stage stage, ViewModelProvider viewModelProvider) {
        this.viewModelProvider = viewModelProvider;
        this.stage = stage;
        viewModelProvider.instantiateViewModels(this);
    }

    /**
     * Метод, который запускает самое первое вью. Оно должно быть показано пользователю
     */
    public void start() {
        openView(LOG_IN_VIEW);
    }

    /**
     * Запускает потоки, которые будут работать в фоне и проверять
     * наличие новых сообщений или чатов
     */
    public void runNextModel() {
        viewModelProvider.runClientModel();
    }

    /**
     * Метод, который открывает указанное с помощью константы вью
     * @param viewType константа указывающая на нужное вью
     */
    public void openView(Views viewType) {
        Settings settings = SettingsManager.getSettings();
        FXMLLoader fxmlLoader = getLoader(viewType);
        Parent root = load(fxmlLoader);

        root.getStylesheets().clear();
        root.getStylesheets().add(String.valueOf(ClientApplication.class.getResource("styles/" + viewType.cssFile + settings.getTheme() + ".css")));

        AbstractView<AbstractViewModel<?>> view = fxmlLoader.getController();
        AbstractViewModel<?> viewModel = getViewModel(viewType);
        view.init(viewModel);

        // создать сцену для нового вью и установить его на stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * открывает заданную панель в заданном контейнере
     * @param paneType тип панели для открытия
     * @param placeHolder контейнер типа Pane или его наследник, на месте которого будет открыта новая панель
     */
    public void openPane(Views paneType, Pane placeHolder) {
        Settings settings = SettingsManager.getSettings();
        FXMLLoader fxmlLoader = getLoader(paneType);
        Parent paneRoot = load(fxmlLoader);

        paneRoot.getStylesheets().clear();
        paneRoot.getStylesheets().add(String.valueOf(ClientApplication.class.getResource("styles/" + paneType.cssFile + settings.getTheme() + ".css")));


        AbstractView<AbstractChildViewModel<?>> pane = fxmlLoader.getController();
        AbstractViewModel<?> viewModel = getViewModel(paneType);
        pane.init((AbstractChildViewModel<?>) viewModel);
        pane.getViewModel().setPlaceHolder(placeHolder);
        pane.getViewModel().actionInParentOnOpen();

        // эта строчка устанавливает уже инициализированную панель на ее место в родителе
        placeHolder.getChildren().setAll(paneRoot);
        if (placeHolder instanceof AnchorPane) {
            anchorPaneInParent(paneRoot);
        }
    }

    private AbstractViewModel<?> getViewModel(Views viewType) {
        return viewType.viewModelGetter.apply(viewModelProvider);
    }

    private static Parent load(FXMLLoader fxmlLoader) {
        try {
            return fxmlLoader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static FXMLLoader getLoader(Views view) {
        return new FXMLLoader(LogInView.class.getResource(view.fxmlFileName + ".fxml"));
    }

    private void anchorPaneInParent(Node child) {
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
    }
}
