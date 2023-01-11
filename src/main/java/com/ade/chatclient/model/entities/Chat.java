package com.ade.chatclient.model.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


// entity used to map users as Java objects
@Getter
@Setter
@ToString
public class Chat {
    private Long id;
    private Boolean isPrivate;
    private List<User> members;
}
