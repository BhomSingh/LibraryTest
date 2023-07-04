package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.ConversationStatusListenerDataModel;
import com.chat.befiler.model.chat.ConversationStatusModel;

public class ConversationStatusEvent {
    public ConversationStatusModel conversationStatusModel;
    public String eventType;

    public ConversationStatusEvent(ConversationStatusModel conversationStatusModel, String eventType) {
        this.conversationStatusModel = conversationStatusModel;
        this.eventType = eventType;
    }
}
