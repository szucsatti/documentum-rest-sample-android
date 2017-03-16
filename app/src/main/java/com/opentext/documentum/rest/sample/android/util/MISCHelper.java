/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;


public class MISCHelper {
    private static String[] tmpIds;
    private static SysObjectListBaseAdapter moveFromAdapter;

    public static SysObjectListBaseAdapter getMoveFromAdapter() {
        return moveFromAdapter;
    }

    public static void setMoveFromAdapter(SysObjectListBaseAdapter moveFromAdapter) {
        MISCHelper.moveFromAdapter = moveFromAdapter;
    }

    public static String[] getTmpIds() {
        return tmpIds;
    }

    public static void setTmpIds(String[] idArray) {
        tmpIds = idArray;
    }
}
