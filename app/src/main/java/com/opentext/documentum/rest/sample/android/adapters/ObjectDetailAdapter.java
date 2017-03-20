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
import com.opentext.documentum.rest.sample.android.util.TypeInfoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectDetailAdapter extends ArrayAdapter<ObjectDetailItem> {
    private int resourceId;
    private List<ObjectDetailItem> items;

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
        filterEnable(items.get(position), contentET);
        return convertView;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public void updateContent(int position, String content) {
        this.items.get(position).setContent(content);
    }

    public void filterEnable(final ObjectDetailItem item, EditText contentET) {
        if (TypeInfoHelper.INSTANCE.editable(item.getType(), item.getProperty(), onCreation)) {
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
                    for (ObjectDetailItem i : ObjectDetailAdapter.this.items)
                        if (i.getProperty().equals(item.getProperty())) {
                            i.setContent(s.toString());
                            break;
                        }
                }
            });
        }
    }

    public Map<String, String> filterEditable() {
        Map<String, String> map = new HashMap<>();
        for (ObjectDetailItem item : this.items)
            if (TypeInfoHelper.INSTANCE.editable(item.getType(), item.getProperty(), onCreation))
                map.put(item.getProperty(), item.getContent());
        return map;
    }

    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();
        for (ObjectDetailItem item : this.items) {
            map.put(item.getProperty(), item.getContent());
        }
        return map;
    }

    public String getObjectName() {
        for (ObjectDetailItem item : items)
            if (item.getProperty().equals(DctmPropertyName.OBJECT_NAME))
                return item.getContent();
        return "";
    }
}
