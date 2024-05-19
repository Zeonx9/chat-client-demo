package com.ade.chatclient.dtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeGroupName {
    private String groupName;
}
