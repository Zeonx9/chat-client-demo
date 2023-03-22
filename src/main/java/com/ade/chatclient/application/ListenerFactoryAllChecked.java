package com.ade.chatclient.application;

import javafx.beans.value.ChangeListener;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class ListenerFactoryAllChecked {
    private final Runnable actionOnCheckPassed;
    private final Runnable actionOnCheckFailed;
    private final List<Boolean> conditions = new ArrayList<>();

    private boolean all() {
        for (Boolean cond : conditions) {
            if (!cond)
                return false;
        }
        return true;
    }

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
}
