package com.ade.chatclient.domain;

import lombok.*;

import java.util.List;

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
    private Long unreadCount;

    public void incrementUnreadCount() {
        if (unreadCount == null) {
            unreadCount = 1L;
        } else {
            unreadCount += 1L;
        }
    }

    public String getChatName() {
        if (group != null) {
            return group.getName();
        }
        var names = members.stream().map(User::getUsername).toList();
        return String.join(", ", names);
    }
}
