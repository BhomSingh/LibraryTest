package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.ConversationStatusListenerDataModel;

public class ConversationStatusListenerEvent {
    public ConversationStatusListenerDataModel message;
    public String eventType;

    public ConversationStatusListenerEvent(ConversationStatusListenerDataModel message, String eventType) {
        this.message = message;
        this.eventType = eventType;
    }
}
