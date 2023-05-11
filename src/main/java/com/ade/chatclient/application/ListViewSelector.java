package com.ade.chatclient.application;

import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListViewSelector<T> {
    private final ListView<T> listView;

    public void select(int index) {
        listView.getSelectionModel().select(index);
    }

    public void select(T object) {
        listView.getSelectionModel().select(object);
    }
}
