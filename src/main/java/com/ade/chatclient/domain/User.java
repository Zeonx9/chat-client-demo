package com.ade.chatclient.domain;

import lombok.*;

/**
 * Класс, который представляет пользователя
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
}
