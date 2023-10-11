package com.ade.chatclient.dtos;

import com.ade.chatclient.domain.GroupChatInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequest {
    @Builder.Default
    private List<Long> ids = new ArrayList<>();
    private GroupChatInfo groupInfo;
}

