package com.ade.chatclient.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, который представляет компанию авторизованного пользователя
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private Long id;
    private String name;
}
