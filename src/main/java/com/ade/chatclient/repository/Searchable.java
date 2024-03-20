package com.ade.chatclient.repository;

import com.ade.chatclient.domain.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Searchable<T> {
    List<T> search(String request);

    default boolean byUserName(User user, String request) {
        AtomicBoolean isRequested = new AtomicBoolean(true);
        String realName = user.getRealName().toLowerCase();
        String surname = user.getSurname().toLowerCase();
        String login = user.getUsername().toLowerCase();
        if (request.contains(" ")) {
            Set<String> splitRequest = new HashSet<>(Arrays.stream(request.split(" ")).toList());
            if (splitRequest.size() > 2) {
                isRequested.set(false);
            }
            splitRequest.forEach(req -> {
                if (!(realName.startsWith(req) || surname.startsWith(req))) {
                    isRequested.set(false);
                }
            });
        } else {
            isRequested.set(login.startsWith(request)
                            || realName.startsWith(request)
                            || surname.startsWith(request)
            );
        }
        return isRequested.get();
    }

    default String processingSearchString(String request) {
        return request.toLowerCase().strip();
    }
}
