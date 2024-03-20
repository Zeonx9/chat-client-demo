package com.ade.chatclient.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Settings {
    @Builder.Default
    private String login = "";
    @Builder.Default
    private String password = "";
    @Builder.Default
    private String theme = "Dark";
}
