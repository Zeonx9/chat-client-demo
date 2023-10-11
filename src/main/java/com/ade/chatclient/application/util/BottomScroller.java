package com.ade.chatclient.application.util;

import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;

/**
 * Вспомогательный класс, который позволяет прокручивать listview из вью-модели, при этом не нарушая инкапсуляцию и
 * архитектуру MVVM.
 */
@RequiredArgsConstructor
public class BottomScroller<T> {
    private final ListView<T> listView;

    /**
     * прокручивает заданный список вниз.
     */
    public void scrollDown() {
        listView.scrollTo(listView.getItems().size() - 1);
    }
}
