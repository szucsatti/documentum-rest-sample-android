/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.items;


import com.opentext.documentum.rest.sample.android.util.TypeInfoHelper;

public class ObjectDetailItem {
    private String type;
    private String property;
    private String content;

    public ObjectDetailItem(String type, String property, String content) {
        this.type = type;
        this.property = property;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getProperty() {
        return property;
    }

    public String getLabel() {
        return TypeInfoHelper.INSTANCE.getLabel(type, property);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
