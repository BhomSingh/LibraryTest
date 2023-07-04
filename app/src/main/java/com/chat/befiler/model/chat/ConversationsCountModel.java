package com.chat.befiler.model.chat;

import java.util.ArrayList;

public class ConversationsCountModel {
    public ArrayList<Conversation> arrayListAllAssign ;
    public ArrayList<Conversation> arrayListASSIGNED ;
    public ArrayList<Conversation> arrayListNew ;
    public ArrayList<Conversation> arrayListResolved ;


    public ConversationsCountModel(ArrayList<Conversation> arrayListAllAssign, ArrayList<Conversation> arrayListASSIGNED, ArrayList<Conversation> arrayListNew, ArrayList<Conversation> arrayListResolved) {
        this.arrayListAllAssign = arrayListAllAssign;
        this.arrayListASSIGNED = arrayListASSIGNED;
        this.arrayListNew = arrayListNew;
        this.arrayListResolved = arrayListResolved;
    }
}
