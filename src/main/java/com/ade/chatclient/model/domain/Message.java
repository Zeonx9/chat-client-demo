package com.ade.chatclient.model.domain;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    private User author;
}

