package com.chat.befiler.model.chat;

import java.util.ArrayList;

public class ConversationByUID {
    public long customerId = 0;
    public String customerEmail = "";
    public long toUserId = 0;
    public long fromUserId = 0;
    public long groupId = 0;
    public long agentId = 0;
    public String content = "";
    public String timestamp = "";
    public String sender = "";
    public String receiver = "";
    public String type = "";
    public String source = "";
    public String groupName = "";
    public long forwardedTo = 0;
    public String customerName = "";
    public String conversationUid = "";
    public int colorCode ;
    public boolean isPrivate;
    public boolean isFromWidget;
    public long tiggerevent = 0;
    public String conversationType = "text";
    public String pageId = "";
    public String pageName = "";
    public String fileLocalUri = "";
    public ArrayList<FilesData> files = new ArrayList<>();
    public boolean isDownloading;


}
