package com.ade.chatclient.application.util;

import javafx.beans.value.ChangeListener;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Вспомогательный класс, который создает связанные между собой обработчики событий.
 * Обработчики выставляют определенные условия.
 * Когда условия выставленные обработчиками выполнятся, то будет выполнено определенное действие.
 * Если же хоть одно условие не удовлетворенно, то будет выполняться другое установленное действие.
 */
@RequiredArgsConstructor
public class ListenerFactoryAllChecked {
    private final Runnable actionOnCheckPassed;
    private final Runnable actionOnCheckFailed;
    private final List<Boolean> conditions = new ArrayList<>();

    /**
     * создает очередной обработчик и додавляет связанное с ним условие в общий список
     * @param checker Функция предикат, которой должно удовлетворять новое значение.
     *                (обычно ссылка на метод класса)
     * @return созданный обработчик для условия.
     * @param <T> - тип значения, которое обработчик проверяет
     */
    public <T> ChangeListener<T> newListener(Function<T, Boolean> checker) {
        int index = conditions.size();
        conditions.add(false);
        return (observableValue, oldVal, newVal) -> {
            conditions.set(index, checker.apply(newVal));
            if (all()) {
                actionOnCheckPassed.run();
            } else {
                actionOnCheckFailed.run();
            }
        };
    }

    private boolean all() {
        for (Boolean cond : conditions) {
            if (!cond)
                return false;
        }
        return true;
    }
}
