package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.Conversation;
import com.chat.befiler.model.chat.EngageListener;

public class EngagedChatEvent {
    public EngageListener engageListener;
    public String eventType;

    public EngagedChatEvent(EngageListener engageListener, String eventType) {
        this.engageListener = engageListener;
        this.eventType = eventType;
    }
}
