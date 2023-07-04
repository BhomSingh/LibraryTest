package com.chat.befiler.model.login.decodeTokenModel;

import java.util.ArrayList;

public class PermissionsData {

    public String RoleId;
    public String RoleName;
    public ArrayList<ListPermissionsData> ListPermissions;
    public boolean IsSuperAdmin;
    public boolean AccessLevel;
    public int actionId;
}
