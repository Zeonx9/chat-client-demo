package com.ade.chatclient.model;

import java.util.List;

public interface ClientModel {
    Long getMyId();
    String getMyName();
    List<String> getMyChats();
    void Authorize(String login);
}
