package com.chat.befiler.Events.appEvents;

import com.chat.befiler.model.chat.ConversationByUID;
import com.chat.befiler.model.chat.FilesData;

import java.util.ArrayList;

public class MessageEventFileDownload {
    public String eventType = "";
    public String conversationUID = "";
    public String documentName;
    public int position;
    public ArrayList<FilesData> files = new ArrayList<>();
    public ConversationByUID conversationByUID ;

    public MessageEventFileDownload(String eventType,String conversationUID, String documentName, ArrayList<FilesData> files,int position,ConversationByUID conversationByUID) {
        this.eventType = eventType;
        this.conversationUID = conversationUID;
        this.documentName = documentName;
        this.files = files;
        this.position = position;
        this.conversationByUID = conversationByUID;
    }
}
