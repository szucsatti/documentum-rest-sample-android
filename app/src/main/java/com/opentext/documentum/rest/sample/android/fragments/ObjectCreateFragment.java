/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;
import com.opentext.documentum.rest.sample.android.items.ObjectDetailItem;
import com.opentext.documentum.rest.sample.android.observables.ObjectCreateObservables;


public class ObjectCreateFragment extends ObjectBaseFragment {
    public static String KEY_ITEM_ID = "ID";
    int menuItemId;

    public static ObjectCreateFragment newInstance(String id, int menuItemId) {
        ObjectCreateFragment instance = new ObjectCreateFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putInt(KEY_ITEM_ID, menuItemId);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // todo: check what's the property to be set
        switch (menuItemId) {
            case R.id.create_cabinet:
            case R.id.create_folder:
            case R.id.create_document:
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(
                                        DctmPropertyName.OBJECT_NAME, "")},
                        true);
                if (menuItemId == R.id.create_document) downloadButton.setVisibility(View.GONE);
                break;
            case R.id.create_user:
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(DctmPropertyName.USER_NAME, ""),
                                new ObjectDetailItem(DctmPropertyName.USER_LOGIN_NAME, ""),
                                new ObjectDetailItem(DctmPropertyName.USER_ADDRESS, ""),
                                new ObjectDetailItem(DctmPropertyName.USER_SOURCE, "inline password"),
                                new ObjectDetailItem(DctmPropertyName.USER_PASSWORD, "")},
                        true);
                break;
            case R.id.create_group:
                updateAdapterItems(createObjectDetailItems(
                        DctmPropertyName.GROUP_NAME, "",
                        DctmPropertyName.DESCRIPTION_GROUP, "",
                        DctmPropertyName.GROUP_DISPLAY_NAME, ""),
                        true);
                setErrorContent();
                buttons.setVisibility(View.GONE);
                break;
        }
        if (menuItemId == R.id.create_folder || menuItemId == R.id.create_cabinet || menuItemId == R.id.create_user || menuItemId == R.id.create_group) {
            setNullContent();
            buttons.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (menuItemId == R.id.check_in_major || menuItemId == R.id.check_in_minor || menuItemId == R.id.check_in_branch)
                    ObjectCreateObservables.checkIn(this);
                else if (menuItemId == R.id.create_cabinet || menuItemId == R.id.create_folder || menuItemId == R.id.create_document || menuItemId == R.id.create_user || menuItemId == R.id.create_group)
                    ObjectCreateObservables.create(this);
                else
                    ;
        }

        return true;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        objectId = args.getString(KEY_ID);
        menuItemId = args.getInt(KEY_ITEM_ID);
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    private ObjectDetailItem[] createObjectDetailItems(String... args) {
        ObjectDetailItem[] items = new ObjectDetailItem[args.length / 2];
        for (int i = 0; i < args.length / 2; ++i)
            items[i] = new ObjectDetailItem(args[2 * i], args[2 * i + 1]);
        return items;
    }
}
