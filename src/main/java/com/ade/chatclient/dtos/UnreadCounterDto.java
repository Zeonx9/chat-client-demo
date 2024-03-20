package com.ade.chatclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCounterDto {
    private Long chatId;
    private Integer count;
}
