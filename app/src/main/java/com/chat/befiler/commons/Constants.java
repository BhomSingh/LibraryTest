package com.chat.befiler.commons;

import com.chat.befiler.model.chat.Conversation;

public class Constants {
    //CHAT HUB CONNECTION URL
    public static String ChatHubUrl = "http://175.107.196.226:5003/chatHub";


    //SELECTED MENU KEYS OR MENU NAMES
    public static String RESOLVED = "2";
    public static String ASSIGNED = "1";
    public static String ALL_ASSIGNED = "3";
    public static String NEW = "0";
    // BUNDLE KEYS
    public static String SELECT_MENU_KEY = "selectMenu";
    public static String CONVERSATION_BY_UID_KEY = "conversationByUID";
    public static String SOURCE_KEY = "source";
    public static String NAME_KEY = "Name";
    public static String NAME_LETTER_KEY = "nameLetter";
    public static String COLOR_CODE_KEY = "colorCode";
    //EVENT BUS KEYS
    public static String HIDE_TOOLBAR = "HideToolbar";
    //
    public static Integer GlobalAccess = 5;
    public static Integer GroupAccess = 5;
    public static Integer RestrictedAccess = 5;
    public static Integer GroupRestricted = 5;
    public static Integer Assign_Group = 6;
    public static Integer Assign_Agent = 7;
    public static Integer Resolve_Conversation = 8;
    public static Integer Reopen_Conversation = 9;




}
