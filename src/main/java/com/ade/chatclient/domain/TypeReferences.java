package com.ade.chatclient.domain;

import com.ade.chatclient.dtos.UnreadCounterDto;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class TypeReferences {
    public static final TypeReference<List<Chat>> ListOfChat = new TypeReference<>() {};
    public static final TypeReference<List<Message>> ListOfMessage = new TypeReference<>() {};
    public static final TypeReference<List<User>> ListOfUser = new TypeReference<>() {};
    public static final TypeReference<List<UnreadCounterDto>> ListOfUnreadCounters = new TypeReference<>() {};

}
