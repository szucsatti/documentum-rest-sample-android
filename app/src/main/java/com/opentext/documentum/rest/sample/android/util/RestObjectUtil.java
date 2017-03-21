/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;


import com.emc.documentum.rest.client.sample.model.RestObject;

public final class RestObjectUtil {
    private RestObjectUtil() {
    }

    public static Object get(RestObject object, String property) {
        return object.getProperties().containsKey(property) ? object.getProperties().get(property) : "";
    }

    public static String getString(RestObject object, String property) {
        return (String) get(object, property);
    }
}
