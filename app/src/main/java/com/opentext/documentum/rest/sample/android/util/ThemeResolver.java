/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;


import android.content.res.Resources;
import android.util.TypedValue;

public final class ThemeResolver {
    private ThemeResolver() {
    }

    public static int resolve(Resources.Theme theme, int attr) {
        final TypedValue value = new TypedValue();
        theme.resolveAttribute(attr, value, true);
        return value.data;
    }
}
