package com.chat.befiler.model.chat;

import com.chat.befiler.model.login.ListPermissionData;

import java.util.ArrayList;

public class PermissionDataModel {

    private int RoleId ;
    private String RoleName ;
    private ArrayList<ListPermissionData> ListPermissions = new ArrayList<>() ;

    public int getRoleId() {
        return RoleId;
    }

    public void setRoleId(int roleId) {
        RoleId = roleId;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public ArrayList<ListPermissionData> getListPermissions() {
        return ListPermissions;
    }

    public void setListPermissions(ArrayList<ListPermissionData> listPermissions) {
        ListPermissions = listPermissions;
    }
}