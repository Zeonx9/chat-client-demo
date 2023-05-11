package com.ade.chatclient.application;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;

public abstract class AbstractDialog<T, M extends AbstractDialogModel<T>> extends Dialog<T> {
    @FXML protected DialogPane dialogPane;
    protected M viewModel;

    private void makeButtonInvisible () {
        Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }

    public void init(M viewModel) {
        this.viewModel = viewModel;
        setResultConverter(viewModel::resultConverter);
        makeButtonInvisible();
    }

    public void onOkClicked() {
        setResult(viewModel.onOkClicked());
    }

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
}
