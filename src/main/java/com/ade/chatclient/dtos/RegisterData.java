package com.ade.chatclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterData {
    private AuthRequest authRequest;
    private String realName;
    private String surname;
    private LocalDate dateOfBirth;
}
