package com.ade.chatclient.dtos;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReadNotification {
    private Long userId;
    private Long chatId;
}
