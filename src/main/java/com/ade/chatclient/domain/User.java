package com.ade.chatclient.domain;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс, который представляет пользователя
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String realName;
    private String surname;
    private LocalDate dateOfBirth;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
