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


public class UsersListAdapter extends SysObjectListBaseAdapter {
    public UsersListAdapter(Context context, int resource, String entranceObjectId, SysObjectNavigationBaseFragment fragment) {
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
        typeImage.setImageResource(R.drawable.vic_user);
        nameTextView.setText(item.entry.getTitle());
        timeTextView.setText(regularTime(item.entry.getUpdated()));


        View aboutImage = convertView.findViewById(R.id.item_about_image);
        aboutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.addObjectDetailFragment(entryList.get(position));
            }
        });

        if (fragment == null) aboutImage.setVisibility(View.GONE);

        if (this.selectedPositions.contains(position))
            convertView.setBackgroundColor(context.getResources().getColor(R.color.selected_cabinet_item));
        else
            convertView.setBackgroundColor(context.getResources().getColor(R.color.pureWhite));

        return convertView;
    }
}
