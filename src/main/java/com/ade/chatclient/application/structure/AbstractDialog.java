package com.ade.chatclient.application.structure;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;

/**
 * Класс, от которого наследуются все классы диалогов
 * @param <T> тип возвращаемого из диалога результата
 * @param <M> тип вьюмодели, который управлет этим диалоговым окном.
 */
public abstract class AbstractDialog<T, M extends AbstractDialogModel<T>> extends Dialog<T> {
    @FXML protected DialogPane dialogPane;
    protected M viewModel;

    /**
     * Метод инициализации диалогового окна: выполняет привязку Dialog и DialogModel, устанавливает ResultConverter и вызывает функцию, которая делает кнопку close невидимой
     * @param viewModel класс DialogModel, соответствующий типу диалогового окна
     */
    public final void init(M viewModel) {
        this.viewModel = viewModel;
        setResultConverter(viewModel::resultConverter);
        setTitle(getTitleString());
        makeButtonInvisible();
        initialize();
    }

    /**
     * метод для обработки клика на кнопку OK
     */
    public final void onOkClicked() {
        setResult(viewModel.onOkClicked());
    }

    /**
     * производит binding всех необходимых property между диалогом и его вьюмоделью.
     */
    protected abstract void initialize();

    /**
     * предоставляет заголовок диалогового окна.
     */
    protected abstract String getTitleString();

    /**
     * Метод выполняет загрузку диалога из fxml файла
     * @param selfClass класс соответствующего диалогового окна
     * @param fxml название файла, в котором описан интерфейс диалогового окна
     * @return контроллер диалогового окна
     */
     protected static <C extends AbstractDialog<?, ?>> C getInstance(Class<C> selfClass, String fxml) {
         FXMLLoader loader = new FXMLLoader(selfClass.getResource(fxml));
         DialogPane pane;
         try {
             pane = loader.load();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         C controller = loader.getController();
         controller.setDialogPane(pane);
         return controller;
    }

    private void makeButtonInvisible () {
        Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }
}
