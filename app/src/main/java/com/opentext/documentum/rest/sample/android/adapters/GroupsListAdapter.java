/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.fragments.SysObjectNavigationBaseFragment;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.util.ThemeResolver;


public class GroupsListAdapter extends SysObjectListBaseAdapter {
    public GroupsListAdapter(Context context, int resource, String entranceObjectId, SysObjectNavigationBaseFragment fragment) {
        super(context, resource, entranceObjectId, fragment);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
        }
        EntryItem item = entryList.get(position);
        ImageView typeImage = (ImageView) convertView.findViewById(R.id.item_cabinetslist_image);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.item_cabinetslist_name);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.item_cabinetslist_time);
        typeImage.setImageResource(R.drawable.vic_group);
        nameTextView.setText(item.entry.getTitle());
        timeTextView.setText(regularTime(item.entry.getUpdated()));

        ImageView aboutImage = (ImageView) convertView.findViewById(R.id.item_about_image);
        aboutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.addObjectDetailFragment(entryList.get(position));
            }
        });

        if (fragment == null)
            aboutImage.setVisibility(View.GONE);

        if (this.selectedPositions.contains(position)) {
            convertView.setElevation(5);
            convertView.setBackgroundColor(context.getColor(R.color.appRowSelected));
            nameTextView.setTextColor(ThemeResolver.resolve(context.getTheme(), R.attr.colorPrimary));
            timeTextView.setTextColor(ThemeResolver.resolve(context.getTheme(), R.attr.colorPrimary));
            aboutImage.setColorFilter(ThemeResolver.resolve(context.getTheme(), R.attr.colorPrimary));
        } else {
            convertView.setElevation(0);
            convertView.setBackgroundColor(ThemeResolver.resolve(context.getTheme(), R.attr.colorPrimary));
            nameTextView.setTextColor(context.getColor(R.color.appTextGray));
            timeTextView.setTextColor(context.getColor(R.color.appSubtitleGray));
            aboutImage.setColorFilter(context.getColor(R.color.appRowSelected));
        }

        return convertView;
    }
}
