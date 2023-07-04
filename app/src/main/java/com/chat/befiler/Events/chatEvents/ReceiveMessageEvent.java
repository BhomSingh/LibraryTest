package com.chat.befiler.Events.chatEvents;

import com.chat.befiler.model.chat.RecieveMessage;

public class ReceiveMessageEvent {
    public RecieveMessage recieveMessage;
    public String eventType;

    public ReceiveMessageEvent(RecieveMessage recieveMessage, String eventType) {
        this.recieveMessage = recieveMessage;
        this.eventType = eventType;
    }
}
