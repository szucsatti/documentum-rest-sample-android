/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import com.opentext.documentum.rest.sample.android.R;

import java.util.HashMap;
import java.util.Map;

public final class MimeIconHelper {
    private static Map<String, Integer> MIME_ICON_MAP = new HashMap<>();

    static {
        MIME_ICON_MAP.put("doc", R.drawable.vic_file_word);
        MIME_ICON_MAP.put("docx", R.drawable.vic_file_word);
        MIME_ICON_MAP.put("ppt", R.drawable.vic_file_ppt);
        MIME_ICON_MAP.put("pptx", R.drawable.vic_file_ppt);
        MIME_ICON_MAP.put("xls", R.drawable.vic_file_excel);
        MIME_ICON_MAP.put("xlst", R.drawable.vic_file_excel);
        MIME_ICON_MAP.put("pdf", R.drawable.vic_file_pdf);
        MIME_ICON_MAP.put("jpg", R.drawable.vic_file_image);
        MIME_ICON_MAP.put("jpeg", R.drawable.vic_file_image);
        MIME_ICON_MAP.put("png", R.drawable.vic_file_image);
        MIME_ICON_MAP.put("bmp", R.drawable.vic_file_image);
        MIME_ICON_MAP.put("mov", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("avi", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("mp4", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("3gp", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("wmv", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("rmvb", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("mkv", R.drawable.vic_file_video);
        MIME_ICON_MAP.put("mp3", R.drawable.vic_file_audio);
        MIME_ICON_MAP.put("aac", R.drawable.vic_file_audio);
        MIME_ICON_MAP.put("wav", R.drawable.vic_file_audio);
        MIME_ICON_MAP.put("wma", R.drawable.vic_file_audio);
        MIME_ICON_MAP.put("aiff", R.drawable.vic_file_audio);
        MIME_ICON_MAP.put("java", R.drawable.vic_file_code);
        MIME_ICON_MAP.put("c", R.drawable.vic_file_code);
        MIME_ICON_MAP.put("css", R.drawable.vic_file_code);
        MIME_ICON_MAP.put("xml", R.drawable.vic_file_code);
        MIME_ICON_MAP.put("json", R.drawable.vic_file_code);
    }

    private MimeIconHelper() {
    }

    public static int getDocIcon(String ext) {
        return MIME_ICON_MAP.containsKey(ext) ? MIME_ICON_MAP.get(ext) : R.drawable.vic_file_doc;
    }
}
