package com.ade.chatclient.application.util;

import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;

/**
 * Вспомогательный класс, который позволяет управлять выделением Listview, притом сохраняя инкапсуляцию.
 * За счет того, что ссылка на объект из вью не передается непосредственно во вьюмодель.
 */
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
