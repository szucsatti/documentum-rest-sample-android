/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;

import com.emc.documentum.rest.client.sample.model.Link;
import com.emc.documentum.rest.client.sample.model.LinkRelation;
import com.emc.documentum.rest.client.sample.model.RestObject;

import java.util.List;

public final class AllowableActionsHelper {
    private AllowableActionsHelper() {
    }

    public static boolean canEdit(RestObject object) {
        return foundLinks(object, LinkRelation.EDIT);
    }

    public static boolean canDelete(RestObject object) {
        return foundLinks(object, LinkRelation.DELETE);
    }

    public static boolean canCheckout(RestObject object) {
        return foundLinks(object, LinkRelation.CHECKOUT);
    }

    public static boolean canCancelCheckout(RestObject object) {
        return foundLinks(object, LinkRelation.CANCEL_CHECKOUT);
    }

    public static boolean canCheckinAsMajor(RestObject object) {
        return foundLinks(object, LinkRelation.CHECKIN_NEXT_MAJOR);
    }

    public static boolean canCheckinAsMinor(RestObject object) {
        return foundLinks(object, LinkRelation.CHECKIN_NEXT_MINOR);
    }

    public static boolean canCheckinAsBranch(RestObject object) {
        return foundLinks(object, LinkRelation.CHECKIN_BRANCH_VERSION);
    }

    public static boolean canCopyFrom(RestObject object) {
        return true;
    }

    public static boolean canCopyTo(RestObject object) {
        return foundLinks(object, LinkRelation.EDIT);
    }

    public static boolean canMoveFrom(RestObject object) {
        return foundLinks(object, LinkRelation.EDIT);
    }

    public static boolean canMoveTo(RestObject object) {
        return foundLinks(object, LinkRelation.EDIT);
    }

    private static boolean foundLinks(RestObject object, LinkRelation... rels) {
        List<Link> links = object.getLinks();
        if (links == null || links.isEmpty()) {
            return false;
        }
        for (Link link : links) {
            for (LinkRelation rel : rels) {
                if (link.getRel().equals(rel.rel())) {
                    return true;
                }
            }
        }
        return false;
    }
}
