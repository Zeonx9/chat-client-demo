package com.ade.chatclient.application;

import javafx.scene.layout.Pane;

public class AbstractChildViewModel<M> extends AbstractViewModel<M> {
    /**
     * ссылка на область в родителе, которую занимает фрагмент.
     */
    protected Pane placeHolder;

    /**
     * Вызывает метод инициализации ViewModel в абстрактном классе
     * @param viewHandler объект управляющий переключениями вью
     * @param model модель, является источником данных
     */
    public AbstractChildViewModel(ViewHandler viewHandler, M model) {
        super(viewHandler, model);
    }

    public void setPlaceHolder(Pane placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void actionInParentOnOpen() {}
}
