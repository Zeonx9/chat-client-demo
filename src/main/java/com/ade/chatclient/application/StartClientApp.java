package com.ade.chatclient.application;

import javafx.stage.Stage;

/**
 * Вспомогательный класс, который помогает настроить приложение во время запуска
 */
public class StartClientApp {
    private static ViewModelProvider viewModelProvider;

    /**
     * Метод, который запрашивает адрес сервера приложения
     * создает фабрики для компонентов приложения (модель, вью, вью-модель)
     * передает управление на ViewHandler
     * @param stage объект Stage, который получен от метода start в главном классе приложения
     */
    public static void start(Stage stage) {

        // создание фабрик для управления слоями приложения
        ModelFactory modelFactory = new ModelFactory();
        modelFactory.injectServerUrl("http://195.133.196.67:8080");


        viewModelProvider = new ViewModelProvider(modelFactory);
        ViewHandler viewHandler = new ViewHandler(stage, viewModelProvider);

        // включает начальное вью
        viewHandler.start();
    }

    /**
     * Останавливает запущенный фоновый поток.
     */
    public static void stop() {
        viewModelProvider.getBackgroundService().stop();
    }

}
