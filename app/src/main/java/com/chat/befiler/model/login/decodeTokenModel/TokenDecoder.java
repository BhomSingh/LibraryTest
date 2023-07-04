package com.chat.befiler.model.login.decodeTokenModel;

import java.util.ArrayList;

public class TokenDecoder {
    public String UserId;
    public String Name;
    public String Email;
    public boolean IsSuperAdmin;
    public boolean AccessLevel;
    public int actionId;
    public PermissionsData Permissions;
    public ArrayList<GroupsData> Groups;
    public String jti;
    public long nbf;
    public long exp;
    public long iat;
    public String iss;
    public String aud;

}
