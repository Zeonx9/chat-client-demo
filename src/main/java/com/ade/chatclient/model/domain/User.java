package com.ade.chatclient.model.domain;

import lombok.*;

// used to map chats as Java objects
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
}
