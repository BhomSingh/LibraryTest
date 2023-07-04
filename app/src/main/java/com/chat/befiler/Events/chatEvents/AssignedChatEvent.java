package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.AssignChatListener;
import com.chat.befiler.model.chat.Conversation;

public class AssignedChatEvent {
    public AssignChatListener assignChatListener;
    public String eventType;

    public AssignedChatEvent(AssignChatListener assignChatListener, String eventType) {
        this.assignChatListener = assignChatListener;
        this.eventType = eventType;
    }
}
