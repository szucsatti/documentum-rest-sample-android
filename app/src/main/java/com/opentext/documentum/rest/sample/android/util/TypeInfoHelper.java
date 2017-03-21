/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;


import com.emc.documentum.rest.client.sample.model.RestType;
import com.opentext.documentum.rest.sample.android.enums.DctmModelType;
import com.opentext.documentum.rest.sample.android.enums.DctmObjectType;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum TypeInfoHelper {
    INSTANCE;

    public static final String SYSOBJECT_VIEW_LIST = "object_name,title,subject,owner_name,r_object_type,r_creation_date,r_modify_date,r_version_label,r_lock_owner,r_object_id";
    public static final String GROUP_VIEW_LIST = "group_name,group_display_name,owner_name,description,group_class,r_modify_date,is_private,is_dynamic,group_global_unique_id,r_object_id";
    public static final String USER_VIEW_LIST = "user_name,user_address,user_login_name,user_login_domain,user_privileges,user_state,default_folder,r_modify_date,last_login_utc_time,user_global_unique_id,r_object_id";

    private Map<String, Map<String, String>> meta = new ConcurrentHashMap<>();
    private Map<String, List<String>> editableProperties = new ConcurrentHashMap<>();
    private Map<String, List<String>> initialProperties = new ConcurrentHashMap<>();
    private Map<String, String> typeWrapper = new HashMap<>();

    TypeInfoHelper() {
        typeWrapper.put(DctmModelType.DOCUMENT, DctmObjectType.DM_SYSOBJECT);
        typeWrapper.put(DctmModelType.OBJECT, DctmObjectType.DM_SYSOBJECT);
        typeWrapper.put(DctmModelType.FOLDER, DctmObjectType.DM_SYSOBJECT);
        typeWrapper.put(DctmModelType.CABINET, DctmObjectType.DM_SYSOBJECT);
        typeWrapper.put(DctmModelType.USER, DctmObjectType.DM_USER);
        typeWrapper.put(DctmModelType.GROUP, DctmObjectType.DM_GROUP);

        editableProperties.put(DctmObjectType.DM_SYSOBJECT, Arrays.asList(
                DctmPropertyName.OBJECT_NAME, DctmPropertyName.TITLE, DctmPropertyName.SUBJECT
        ));
        editableProperties.put(DctmObjectType.DM_USER, Arrays.asList(
                DctmPropertyName.USER_ADDRESS, DctmPropertyName.USER_LOGIN_NAME, DctmPropertyName.USER_LOGIN_DOMAIN, DctmPropertyName.DEFAULT_FOLDER
        ));
        editableProperties.put(DctmObjectType.DM_GROUP, Arrays.asList(
                DctmPropertyName.GROUP_DISPLAY_NAME, DctmPropertyName.DESCRIPTION_GROUP
        ));
        initialProperties.put(DctmObjectType.DM_SYSOBJECT, Arrays.asList(
                DctmPropertyName.OBJECT_NAME, DctmPropertyName.TITLE, DctmPropertyName.SUBJECT
        ));
        initialProperties.put(DctmObjectType.DM_USER, Arrays.asList(
                DctmPropertyName.USER_NAME, DctmPropertyName.USER_ADDRESS, DctmPropertyName.USER_LOGIN_NAME, DctmPropertyName.USER_LOGIN_DOMAIN, DctmPropertyName.USER_PASSWORD, DctmPropertyName.USER_SOURCE
        ));
        initialProperties.put(DctmObjectType.DM_GROUP, Arrays.asList(
                DctmPropertyName.GROUP_NAME, DctmPropertyName.GROUP_DISPLAY_NAME, DctmPropertyName.DESCRIPTION_GROUP
        ));
    }

    public void setupTypeInfo(RestType type) {
        if (!hasType(type.getName())) {
            meta.put(type.getName(), new HashMap<String, String>());
        }
        Map<String, String> metaProperties = meta.get(type.getName());
        List<Map<String, Object>> properties = type.getProperties();
        for (Map<String, Object> m : properties) {
            metaProperties.put((String) m.get("name"), (String) m.get("label"));
        }
        metaProperties.put(DctmPropertyName.R_OBJECT_ID, "Object ID");
    }

    public boolean hasType(String type) {
        String wrapped = wrap(type);
        return meta.containsKey(wrapped);
    }

    public String getLabel(String type, String property) {
        String wrapped = wrap(type);
        if (!hasType(wrapped) || !meta.get(wrapped).containsKey(property)) {
            return property;
        }
        return meta.get(wrapped).get(property);
    }

    public boolean editable(String type, String property, boolean onCreation) {
        String wrapped = wrap(type);
        boolean flag;
        if ((DctmModelType.FOLDER.equals(wrapped) || DctmModelType.GROUP.equals(wrapped)) && !AppCurrentUser.canCreateUserGroup()) {
            flag = false;
        } else {
            if (onCreation) {
                flag = initialProperties.containsKey(wrapped) && initialProperties.get(wrapped).contains(property);
            } else {
                flag = editableProperties.containsKey(wrapped) && editableProperties.get(wrapped).contains(property);
            }
        }
        return flag;
    }

    private String wrap(String type) {
        return typeWrapper.containsKey(type) ? typeWrapper.get(type) : type;
    }
}
