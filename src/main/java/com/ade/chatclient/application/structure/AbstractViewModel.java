package com.ade.chatclient.application.structure;

import com.ade.chatclient.application.ViewHandler;

/**
 * Класс предназначенный для наследования классами вью-модел.
 * Имеет ссылку на ViewHandler, для того, чтобы переключаться между представленями
 * @param <M> класс модели, который используется внутри
 */
public class AbstractViewModel<M> {
    /**
     * объект управляющий переключениями вью
     */
    protected final ViewHandler viewHandler;
    /**
     * модель является источником данных
     */
    protected final M model;

    /**
     * Ввыполняет привязку модели и объекта управляющего переключениями View к ViewModel
     */
    public AbstractViewModel(ViewHandler viewHandler, M model) {
        this.viewHandler = viewHandler;
        this.model = model;
    }
}
