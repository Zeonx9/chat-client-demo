package com.ade.chatclient.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Класс, который представляет сообщение
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    private User author;
}

