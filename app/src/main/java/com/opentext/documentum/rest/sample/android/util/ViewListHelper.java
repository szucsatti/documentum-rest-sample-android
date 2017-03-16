/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import java.util.HashMap;
import java.util.Map;

public final class ViewListHelper {
    public static final String SYSOBJECT_VIEW_LIST = "object_name,title,subject,owner_name,r_object_type,r_creation_date,r_modify_date,keywords,authors,r_lock_owner,r_object_id";
    public static final String GROUP_VIEW_LIST = "group_name,group_display_name,owner_name,description,group_class,r_modify_date,is_private,is_dynamic,group_global_unique_id,r_object_id";
    public static final String USER_VIEW_LIST = "user_name,user_address,user_login_name,user_login_domain,user_privileges,user_state,default_folder,r_modify_date,last_login_utc_time,user_global_unique_id,r_object_id";
    private static Map<String, String> TYPE_ATTR_VIEW = new HashMap<>();

    static {
        TYPE_ATTR_VIEW.put("object", SYSOBJECT_VIEW_LIST);
        TYPE_ATTR_VIEW.put("document", SYSOBJECT_VIEW_LIST);
        TYPE_ATTR_VIEW.put("folder", SYSOBJECT_VIEW_LIST);
        TYPE_ATTR_VIEW.put("user", USER_VIEW_LIST);
        TYPE_ATTR_VIEW.put("group", GROUP_VIEW_LIST);
    }

    private ViewListHelper() {
    }

    public static String getView(String type) {
        return TYPE_ATTR_VIEW.containsKey(type) ? TYPE_ATTR_VIEW.get(type) : ":default";
    }
}
