/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import com.emc.documentum.rest.client.sample.model.RestObject;

public final class AppCurrentUser {
    private static RestObject CURRENT_USER;

    private AppCurrentUser() {
    }

    public static synchronized void set(RestObject currentUser) {
        CURRENT_USER = currentUser;
    }

    public static String getUsername() {
        return (String) CURRENT_USER.getProperties().get("user_name");
    }

    public static boolean canCreateCabinet() {
        int priviledges = (int) CURRENT_USER.getProperties().get("user_privileges");
        return priviledges >= 2;
    }

    public static boolean canCreateUserGroup() {
        int priviledges = (int) CURRENT_USER.getProperties().get("user_privileges");
        return priviledges >= 4;
    }

    public static boolean isAdmin() {
        int priviledges = (int) CURRENT_USER.getProperties().get("user_privileges");
        return priviledges >= 8;
    }

    public static boolean isSU() {
        int priviledges = (int) CURRENT_USER.getProperties().get("user_privileges");
        return priviledges == 16;
    }
}
