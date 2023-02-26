package com.ade.chatclient.dtos;

import com.ade.chatclient.domain.User;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
}
