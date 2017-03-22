/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;


import android.os.Environment;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.Locale;

public final class FileUtil {
    private FileUtil() {
    }

    public static File newFile(String name, Fragment fragment) {
        File directory = new File(fragment.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
        if (!directory.isDirectory() && !directory.mkdirs()) {
            return null;
        }
        File file = new File(directory, name);
        int count = 1;
        int dotPos = name.lastIndexOf(".");
        String prefix = dotPos > 0 ? name.substring(0, dotPos) : name;
        String suffix = dotPos > 0 ? name.substring(dotPos) : "";
        while (file.exists()) {
            file = new File(directory, String.format(Locale.ENGLISH, "%s(%d)%s", prefix, count++, suffix));
        }
        return file;
    }
}
