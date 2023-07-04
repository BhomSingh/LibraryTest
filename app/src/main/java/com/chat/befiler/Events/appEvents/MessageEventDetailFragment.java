package com.chat.befiler.Events.appEvents;

public class MessageEventDetailFragment {
    public String name;
    public String nameFirstLetter;
    public String source;
    public String colorCodeStr;
    public String conversationByUID;
    public boolean isCalledFromDetailList;

    public MessageEventDetailFragment(String name, String nameFirstLetter, String source, String colorCodeStr,String conversationByUID,boolean isCalledFromDetailList) {
        this.name = name;
        this.nameFirstLetter = nameFirstLetter;
        this.source = source;
        this.colorCodeStr = colorCodeStr;
        this.conversationByUID = conversationByUID;
        this.isCalledFromDetailList = isCalledFromDetailList;
    }
}
