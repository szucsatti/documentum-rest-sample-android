/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.items;

import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;


public class EntryItem {
    public Entry<RestObject> entry;
    public boolean copied, moved, checkedOutByU, checkedOutByOthers;

    public EntryItem(Entry<RestObject> entry) {
        this.entry = entry;
        String r_lock_owner = "";
        if (entry.getContentObject().getProperties() == null)
            r_lock_owner = "";
        else if (entry.getContentObject().getProperties().get("r_lock_owner") != null)
            r_lock_owner = (String) entry.getContentObject().getProperties().get("r_lock_owner");
        if (r_lock_owner.trim().length() == 0) return;
        if (AppCurrentUser.getUsername().equals(r_lock_owner)) checkedOutByU = true;
        else checkedOutByOthers = true;
    }
}
