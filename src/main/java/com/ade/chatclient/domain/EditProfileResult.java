package com.ade.chatclient.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileResult {
    private User user;
    private File file;
}
