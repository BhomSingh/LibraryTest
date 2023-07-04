package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.EngageListener;
import com.chat.befiler.model.chat.SendMessageModel;

public class SendChatEvent {
    public SendMessageModel sendMessageModel;
    public String eventType;

    public SendChatEvent(SendMessageModel sendMessageModel, String eventType) {
        this.sendMessageModel = sendMessageModel;
        this.eventType = eventType;
    }
}
