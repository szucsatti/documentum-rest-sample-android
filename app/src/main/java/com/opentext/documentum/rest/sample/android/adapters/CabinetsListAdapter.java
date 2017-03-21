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

import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.fragments.SysObjectNavigationBaseFragment;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.util.AccountHelper;
import com.opentext.documentum.rest.sample.android.util.MimeIconHelper;

import org.springframework.util.StringUtils;


public class CabinetsListAdapter extends SysObjectListBaseAdapter {

    public CabinetsListAdapter(Context context, int resource, RestObject entranceFolder, String entranceObjectId, SysObjectNavigationBaseFragment fragment) {
        super(context, resource, entranceFolder, entranceObjectId, fragment);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
        }
        EntryItem item = entryList.get(position);
        ImageView typeImage = (ImageView) convertView.findViewById(R.id.item_cabinetslist_image);
        ImageView lockImage = (ImageView) convertView.findViewById(R.id.item_lock_image);
        lockImage.setVisibility(View.GONE);

        if (item.entry.getContentObject().getProperties().containsKey("r_lock_owner")) {
            String owner = item.entry.getContentObject().getProperties().get("r_lock_owner").toString();
            if (!StringUtils.isEmpty(owner)) {
                lockImage.setVisibility(View.VISIBLE);
                if (owner.equals(AccountHelper.getId(context))) {
                    lockImage.setImageResource(R.drawable.vic_lock_o);
                } else {
                    lockImage.setImageResource(R.drawable.vic_lock_b);
                }
            }
        } else {
            lockImage.setVisibility(View.GONE);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.item_cabinetslist_name);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.item_cabinetslist_time);
        switch (item.entry.getContentObject().getType()) {
            case "dm_cabinet":
                typeImage.setImageResource(R.drawable.vic_folder);
                break;
            case "dm_folder":
                typeImage.setImageResource(R.drawable.vic_folder_s);
                break;
            case "dm_document":
                resolveDocIcon(typeImage, item.entry.getContentObject());
                break;
            default:
                typeImage.setImageResource(R.drawable.vic_file);
                break;
        }
        nameTextView.setText(item.entry.getTitle());
        timeTextView.setText(regularTime(item.entry.getUpdated()));

        View aboutImage = convertView.findViewById(R.id.item_about_image);
        aboutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.addObjectDetailFragment(entryList.get(position));
            }
        });

        if (this.selectedPositions.contains(position))
            convertView.setBackgroundColor(context.getResources().getColor(R.color.selected_cabinet_item));
        else
            convertView.setBackgroundColor(context.getResources().getColor(R.color.pureWhite));

        if (position == getCount() - 1) {
            addNextPageFeed();
            notifyDataSetChanged();
        }
        return convertView;
    }

    private void resolveDocIcon(ImageView typeImage, RestObject contentObject) {
        String fileName = (String) contentObject.getProperties().get("object_name");
        int index = fileName.lastIndexOf(".");
        String ext = index < 0 ? "" : fileName.substring(index + 1);
        typeImage.setImageResource(MimeIconHelper.getDocIcon(ext));
    }
}
