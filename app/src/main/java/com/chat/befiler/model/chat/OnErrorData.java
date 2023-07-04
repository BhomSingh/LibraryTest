package com.chat.befiler.model.chat;

public class OnErrorData {
    public RecieveMessage recieveMessage;
    public String eventType;

    public OnErrorData(RecieveMessage recieveMessage, String eventType) {
        this.recieveMessage = recieveMessage;
        this.eventType = eventType;
    }
}
