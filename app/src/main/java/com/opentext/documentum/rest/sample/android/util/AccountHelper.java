/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import android.content.Context;
import android.content.SharedPreferences;


public class AccountHelper {
    private static final String PREFERENCE_NAME = "account";

    private static final String KEY_CONTEXT_ROOT = "CONTEXT_ROOT";
    private static final String KEY_ID = "ID";
    private static final String KEY_PASSWORD = "PASSWORD";
    private static final String KEY_REPO = "REPO";
    private static final String KEY_REMEMBER = "REMEMBER";
    private static final String KEY_PRODUCT_MAJOR = "PRODUCT_MAJOR";

    public static String getContextRoot(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(KEY_CONTEXT_ROOT, "");
    }

    public static String getId(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(KEY_ID, "");
    }

    public static String getPassword(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(KEY_PASSWORD, "");
    }

    public static String getREPO(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(KEY_REPO, "");
    }

    public static boolean getRemember(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(KEY_REMEMBER, false);
    }

    public static float getProductMajor(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getFloat(KEY_PRODUCT_MAJOR, 0);
    }

    public static void setProductMajor(Context context, float major) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_PRODUCT_MAJOR, major);
    }

    public static void setAccountAndBuilder(Context context, boolean remember, String contextRoot, String id, String password, String repo) {
        AppDCTMClientBuilder.contextRoot(contextRoot).repository(repo).credentials(id, password);
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        if (!remember) {
            editor.clear().commit();
            return;
        }
        editor.putBoolean(KEY_REMEMBER, true);
        editor.putString(KEY_CONTEXT_ROOT, contextRoot);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_REPO, repo);
        editor.commit();
    }

}
