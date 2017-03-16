/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBinding;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBuilder;


public class AppDCTMClientBuilder {
    private static DCTMRestClient client;
    private static DCTMRestClientBuilder builder;
    private static AppDCTMClientBuilder mockBuilder;
    private static boolean CHANGED = true;
    private static String root;
    private static String repo;
    private static String username;
    private static String password;

    static {
        resetBuilder();
    }

    private AppDCTMClientBuilder() {
    }

    public static String getRoot() {
        return root;
    }

    public static String getRepo() {
        return repo;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void resetBuilder() {
        CHANGED = true;
        builder = new DCTMRestClientBuilder().bind(DCTMRestClientBinding.JSON)
                .debug(true)
                .useFormatExtension(false)
                .ignoreAuthenticateServer(true);
//                .ignoreSslWarning(true);
    }

    public static AppDCTMClientBuilder contextRoot(String s) {
        CHANGED = true;
        root = s;
        builder.contextRoot(s);
        return mockBuilder;
    }

    public static AppDCTMClientBuilder repository(String s) {
        CHANGED = true;
        repo = s;
        builder.repository(s);
        return mockBuilder;
    }

    public static AppDCTMClientBuilder credentials(String username, String password) {
        CHANGED = true;
        AppDCTMClientBuilder.username = username;
        AppDCTMClientBuilder.password = password;
        builder.credentials(username, password);
        return mockBuilder;
    }

    public static DCTMRestClient build() {
        if (CHANGED || client == null) {
            CHANGED = false;
            try {
                client = builder.build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
