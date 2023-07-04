package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.Conversation;

public class ConversationEvent {
    public Conversation conversation;
    public String eventType;

    public ConversationEvent(Conversation conversation, String eventType) {
        this.conversation = conversation;
        this.eventType = eventType;
    }
}
