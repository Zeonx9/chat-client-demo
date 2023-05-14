package com.ade.chatclient.application.util;

import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.Views;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;

/**
 * Вспомогательный класс, который помогает переключать фрагменты внутри родительского вью.
 * Сохраняет инкапсуляцию, скрывает ссылку на объект вью от вьюмодел.
 */
@RequiredArgsConstructor
public class PaneSwitcher {
    private final ViewHandler viewHandler;
    private final Pane placeHolder;

    /**
     * переключить фрагмент
     * @param viewType тип нового фрагмента
     */
    public void switchTo(Views viewType) {
        viewHandler.openPane(viewType, placeHolder);
    }
}
