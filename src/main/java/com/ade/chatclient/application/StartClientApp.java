package com.ade.chatclient.application;

import javafx.stage.Stage;

/**
 * Вспомогательный класс, который помогает настроить приложение во время запуска
 */
public class StartClientApp {
    private static ViewModelProvider viewModelProvider;
    // Содержит набор url для обращения к серверу, используется статически в других местах приложения
    public final static ChatUrls URLS = ChatUrls.DEV;

    /**
     * Метод, который запрашивает адрес сервера приложения
     * создает фабрики для компонентов приложения (модель, вью, вью-модель)
     * передает управление на ViewHandler
     * @param stage объект Stage, который получен от метода start в главном классе приложения
     */
    public static void start(Stage stage) {
        ApiFactory apiFactory = new ApiFactory();
        RepositoryFactory repositoryFactory = new RepositoryFactory(apiFactory);
        // создание фабрик для управления слоями приложения
        ModelFactory modelFactory = new ModelFactory(apiFactory, repositoryFactory);

        viewModelProvider = new ViewModelProvider(modelFactory);
        ViewHandler viewHandler = new ViewHandler(stage, viewModelProvider);

        // включает начальное вью
        viewHandler.start();
    }

    /**
     * Останавливает запущенный фоновый поток.
     */
    public static void stop() {
//        viewModelProvider.getBackgroundService().stop();
        viewModelProvider.getModelFactory().getModel().stopWebSocketConnection();
    }

}
