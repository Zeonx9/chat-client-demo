package com.ade.chatclient.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// used to map chats as Java objects
@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String name;
}
