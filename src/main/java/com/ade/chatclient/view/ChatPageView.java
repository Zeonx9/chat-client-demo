package com.ade.chatclient.view;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ChatPageView {

    public List<String> listChats = List.of("Egor", "Dasha", "Artem");

    public VBox mainVBox = new VBox();

    public void onShowClicked(ActionEvent actionEvent) {
        mainVBox.getChildren().clear();
        listChats.forEach(chat -> {
            SplitPane nsp = new SplitPane();
            Label nl = new Label(chat + '\n');
            nl.setStyle("-fx-font-size:24");
            nsp.getItems().add(new Label("chat:    "));
            nsp.getItems().add(nl);
            mainVBox.getChildren().add(nsp);
        });
    }
}
