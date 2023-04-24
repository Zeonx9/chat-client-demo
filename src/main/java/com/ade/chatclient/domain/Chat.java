package com.ade.chatclient.domain;

import lombok.*;

import java.util.List;
import java.util.Objects;

/**
 * Класс, который представляет чат между двумя пользователями или беседу
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    private Long id;
    private Boolean isPrivate;
    private List<User> members;
    private GroupChatInfo group;
    private Message lastMessage;
    private int unreadCount;

    public void incrementUnreadCount() {
        unreadCount++;
    }

    public boolean isUnreadChat() {
        return unreadCount > 0;
    }

    public String getChatName() {
        if (group != null) {
            return group.getName();
        }
        var names = members.stream().map(User::getUsername).toList();
        return String.join(", ", names);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chat chat)) return false;
        return id.equals(chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
