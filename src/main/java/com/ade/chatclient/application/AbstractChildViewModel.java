package com.ade.chatclient.application;

import javafx.scene.layout.Pane;

public class AbstractChildViewModel<M> extends AbstractViewModel<M> {
    /**
     * ссылка на область в родителе, которую занимает фрагмент.
     */
    protected Pane placeHolder;

    public AbstractChildViewModel(ViewHandler viewHandler, M model) {
        super(viewHandler, model);
    }

    public void setPlaceHolder(Pane placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void actionInParentOnOpen() {}
}
