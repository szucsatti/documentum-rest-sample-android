/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;
import com.opentext.documentum.rest.sample.android.items.ObjectDetailItem;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ObjectDetailAdapter extends ArrayAdapter<ObjectDetailItem> {
    static Set<String> userGroupFilterLabels = new HashSet<>();
    static Set<String> userGroupInitiableLabels = new HashSet<>();
    static Set<String> sysobjectFilterLabels = new HashSet<>();

    static {
        userGroupFilterLabels.add(DctmPropertyName.USER_LOGIN_DOMAIN);
        userGroupFilterLabels.add(DctmPropertyName.USER_LOGIN_NAME);
        userGroupFilterLabels.add(DctmPropertyName.USER_ADDRESS);
        userGroupFilterLabels.add(DctmPropertyName.DEFAULT_FOLDER);
        userGroupFilterLabels.add(DctmPropertyName.DESCRIPTION_GROUP);
        userGroupFilterLabels.add(DctmPropertyName.GROUP_DISPLAY_NAME);

        userGroupInitiableLabels.add(DctmPropertyName.USER_NAME);
        userGroupInitiableLabels.add(DctmPropertyName.USER_SOURCE);
        userGroupInitiableLabels.add(DctmPropertyName.USER_PASSWORD);
        userGroupInitiableLabels.add(DctmPropertyName.GROUP_NAME);

        sysobjectFilterLabels.add(DctmPropertyName.OBJECT_NAME);
        sysobjectFilterLabels.add(DctmPropertyName.TITLE);
        sysobjectFilterLabels.add(DctmPropertyName.SUBJECT);
    }

    private int resourceId;
    private List<ObjectDetailItem> items;

    //    public void updateItems(List<ObjectDetailItem> items) {
//        this.items.clear();
//        for (ObjectDetailItem item : items)
//            this.items.add(item);
//    }
    private Context context;
    private boolean onCreation;

    public ObjectDetailAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resourceId = resource;
        this.items = new ArrayList<>();
    }

    public void updateItems(ObjectDetailItem[] items, boolean onCreation) {
        this.onCreation = onCreation;
        this.items.clear();
        for (ObjectDetailItem item : items)
            this.items.add(item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resourceId, parent, false);
        final EditText labelET = (EditText) convertView.findViewById(R.id.label);
        final EditText contentET = (EditText) convertView.findViewById(R.id.content);

        labelET.getText().clear();
        labelET.getText().append(items.get(position).getLabel());

        contentET.getText().clear();
        contentET.getText().append(items.get(position).getContent());
        filterEnable(labelET.getText().toString(), contentET);
        return convertView;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public void updateContent(int positon, String content) {
        this.items.get(positon).setContent(content);
    }

    public void filterEnable(final String label, EditText contentET) {
        if (editable(label)) {
            contentET.setEnabled(true);
            contentET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    for (ObjectDetailItem item : ObjectDetailAdapter.this.items)
                        if (item.getLabel().equals(label)) {
                            item.setContent(s.toString());
                            break;
                        }
                }
            });
        }
    }

    private boolean editable(String label) {
        if (onCreation && userGroupInitiableLabels.contains(label)) {
            return true;
        }
        if (userGroupFilterLabels.contains(label) && AppCurrentUser.canCreateUserGroup()) {
            return true;
        }
        if (sysobjectFilterLabels.contains(label)) {
            //todo: permission aware??
            return true;
        }
        return false;
    }

    public Map<String, String> getChangableProperties() {
        Map<String, String> map = new HashMap<>();
        for (ObjectDetailItem item : this.items)
            if (userGroupFilterLabels.contains(item.getLabel())
                    || sysobjectFilterLabels.contains(item.getLabel())
                    || (onCreation && userGroupInitiableLabels.contains(item.getLabel())))
                map.put(item.getLabel(), item.getContent());
        return map;
    }

    public Map<String, String> getInitiableProperties() {
        Map<String, String> map = new HashMap<>();
        for (ObjectDetailItem item : this.items)
            if (userGroupFilterLabels.contains(item.getLabel())
                    || sysobjectFilterLabels.contains(item.getLabel())
                    || (onCreation && userGroupInitiableLabels.contains(item.getLabel())))
                map.put(item.getLabel(), item.getContent());
        return map;
    }

    public String getObjectName() {
        for (ObjectDetailItem item : items)
            if (item.getLabel().equals("object_name"))
                return item.getContent();
        return "";
    }
}
