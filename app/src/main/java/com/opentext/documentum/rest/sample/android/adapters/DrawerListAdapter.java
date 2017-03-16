/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.items.DrawerItem;


public class DrawerListAdapter extends ArrayAdapter<DrawerItem> {
    private Context context;
    private int resourceId;
    private DrawerItem[] items;
    private int selected;

    public DrawerListAdapter(Context context, int resource, DrawerItem[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.items = objects;
        this.selected = 0x3f3f3f3f;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(resourceId, parent, false);
        DrawerItem item = items[position];
        ImageView imageView = (ImageView) convertView.findViewById(R.id.drawer_item_image);
        TextView textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
        if (position != selected) {
            textView.setText(item.navString);
            textView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            textView.setTypeface(null, Typeface.NORMAL);
            imageView.setImageResource(item.inactiveResource);
        } else {
            textView.setText(item.navString);
            textView.setTextColor(context.getResources().getColor(R.color.selectedText));
            textView.setTypeface(null, Typeface.BOLD);
            imageView.setImageResource(item.activeResource);
        }
        return convertView;
    }

    public void setSelected(int position) {
        this.selected = position;
    }
}
