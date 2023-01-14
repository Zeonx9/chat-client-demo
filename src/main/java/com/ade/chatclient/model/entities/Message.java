package com.ade.chatclient.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Message {
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    private User author;
}

