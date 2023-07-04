package com.chat.befiler.model.chat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Conversation {
    public long customerId = 0;
    public String customerConnectionId = "";
    public String customerEmail = "";
    public long toUserId = 0;
    public String status = "";

    public long fromUserId = 0;
    public long groupId = 0;
    public long conversationId = 0;
    public String content = "";
    public String timestamp = "";
    public String sender = "";
    public String receiver = "";
    public String type = "";
    public String source = "";
    public String groupName = "";
    public String forwardedTo = "";
    public String customerName = "";
    public String conversationUid = "";
    public long colorCode ;
    public boolean isAgentReplied;
    public boolean isResolved;
    public long childConversationCount = 0;
    public String conversationType = "text";
    public String pageId = "";
    public String pageName = "";
    public ArrayList<FilesData> files = new ArrayList<>();
    public boolean isRecordUpdated;
    public boolean isNewMessageReceive;



}
