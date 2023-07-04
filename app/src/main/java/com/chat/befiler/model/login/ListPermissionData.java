package com.chat.befiler.model.login;

public class ListPermissionData {

    public int ActionId;
    public int AccessLevelCode;
    public String AccessLevel;

    public int getActionId() {
        return ActionId;
    }

    public void setActionId(int actionId) {
        ActionId = actionId;
    }

    public int getAccessLevelCode() {
        return AccessLevelCode;
    }

    public void setAccessLevelCode(int accessLevelCode) {
        AccessLevelCode = accessLevelCode;
    }

    public String getAccessLevel() {
        return AccessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        AccessLevel = accessLevel;
    }
}
