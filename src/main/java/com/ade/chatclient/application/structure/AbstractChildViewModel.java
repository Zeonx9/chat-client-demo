package com.ade.chatclient.application.structure;

import com.ade.chatclient.application.ViewHandler;
import javafx.scene.layout.Pane;

/**
 * Класс предназначенный для наследования классами вью-модел, которые используются для управления фрагментами.
 * Имеют ссылку на макет, в который вставленны, данный макет должен быть вставлен в объектв через сеттер.
 */
public class AbstractChildViewModel<M> extends AbstractViewModel<M> {
    /**
     * ссылка на область в родителе, которую занимает фрагмент.
     */
    protected Pane placeHolder;

    public AbstractChildViewModel(ViewHandler viewHandler, M model) {
        super(viewHandler, model);
    }

    public final void setPlaceHolder(Pane placeHolder) {
        this.placeHolder = placeHolder;
    }

    /**
     * метод, который вызвается при обновлении фрагмента его родителем
     */
    public void actionInParentOnOpen() {}
}
