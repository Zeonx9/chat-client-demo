package com.ade.chatclient.domain;

import com.ade.chatclient.dtos.ChangeGroupName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditChatResult  {
    private Long chatId;
    private ChangeGroupName changeGroupName;
    private File file;
}
