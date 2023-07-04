package com.chat.befiler.retrofit;

import com.chat.befiler.model.chat.GroupsArrayModel;
import com.chat.befiler.model.chat.GroupsDataModel;
import com.chat.befiler.model.chat.UserArrayModel;
import com.chat.befiler.model.login.decodeTokenModel.GroupsData;

import java.util.ArrayList;

public class GroupsByUserDataModel {

    private ArrayList<GroupsArrayModel> groupsList = new ArrayList<>();
    private ArrayList<UserArrayModel> usersList = new ArrayList<>();

    public ArrayList<GroupsArrayModel> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(ArrayList<GroupsArrayModel> groupsList) {
        this.groupsList = groupsList;
    }

    public ArrayList<UserArrayModel> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<UserArrayModel> usersList) {
        this.usersList = usersList;
    }
}
