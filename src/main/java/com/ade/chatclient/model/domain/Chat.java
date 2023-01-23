package com.ade.chatclient.model.domain;

import lombok.*;

import java.util.List;


// entity used to map users as Java objects
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    private Long id;
    private Boolean isPrivate;
    private List<User> members;
}
