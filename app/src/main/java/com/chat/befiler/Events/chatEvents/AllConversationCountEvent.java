package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.ConversationsCountModel;

public class AllConversationCountEvent {
    public ConversationsCountModel conversationsCountModel;
    public String eventType;

    public AllConversationCountEvent(ConversationsCountModel conversationsCountModel, String eventType) {
        this.conversationsCountModel = conversationsCountModel;
        this.eventType = eventType;
    }
}
