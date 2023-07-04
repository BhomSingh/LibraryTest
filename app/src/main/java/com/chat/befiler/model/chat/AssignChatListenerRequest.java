package com.chat.befiler.model.chat;

import java.util.ArrayList;

public class AssignChatListenerRequest {
    public ArrayList<Long> groupIds;
    public long conversationId;
    public String groupName;
    public long agentId;
    public String eventType;

    public AssignChatListenerRequest(ArrayList<Long> groupIds, long conversationId, String groupName, long agentId, String eventType) {
        this.groupIds = groupIds;
        this.conversationId = conversationId;
        this.groupName = groupName;
        this.agentId = agentId;
        this.eventType = eventType;
    }
}



