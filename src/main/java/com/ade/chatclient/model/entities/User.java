package com.ade.chatclient.model.entities;

import lombok.*;

// used to map chats as Java objects
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
}
