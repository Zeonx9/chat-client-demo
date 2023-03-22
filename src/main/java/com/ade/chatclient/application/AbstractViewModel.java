package com.ade.chatclient.application;

public class AbstractViewModel<M> {
    /**
     * объект управляющий переключениями вью
     */
    protected final ViewHandler viewHandler;
    /**
     * модель является источником данных
     */
    protected final M model;

    public AbstractViewModel(ViewHandler viewHandler, M model) {
        this.viewHandler = viewHandler;
        this.model = model;
    }
}
