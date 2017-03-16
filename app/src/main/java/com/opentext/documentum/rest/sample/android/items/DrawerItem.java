/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.items;


public class DrawerItem {
    public String navString;
    public int activeResource;
    public int inactiveResource;

    public DrawerItem(String s, int r1, int r2) {
        this.navString = s;
        this.activeResource = r1;
        this.inactiveResource = r2;
    }
}
