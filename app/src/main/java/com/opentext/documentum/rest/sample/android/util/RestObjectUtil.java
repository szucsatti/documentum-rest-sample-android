/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;


import com.emc.documentum.rest.client.sample.model.RestObject;

public final class RestObjectUtil {
    private RestObjectUtil() {
    }

    public static Object get(RestObject object, String property) {
        return object.getProperties().containsKey(property) ? object.getProperties().get(property) : null;
    }

    public static String getString(RestObject object, String property) {
        Object v = get(object, property);
        return v == null ? "" : (String) v;
    }

    public static int getInt(RestObject object, String property) {
        Object v = get(object, property);
        return v == null ? 0 : (int) v;
    }
}
