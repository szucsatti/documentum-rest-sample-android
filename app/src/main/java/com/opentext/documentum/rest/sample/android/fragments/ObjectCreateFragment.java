/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.DctmModelType;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;
import com.opentext.documentum.rest.sample.android.items.ObjectDetailItem;
import com.opentext.documentum.rest.sample.android.observables.ObjectCreateObservables;


public class ObjectCreateFragment extends ObjectBaseFragment {
    public static String KEY_ITEM_ID = "ID";
    int menuItemId;
    SysObjectListBaseAdapter sourceAdapter;
    BaseUIInterface sourceUiInterface;

    public static ObjectCreateFragment newInstance(String id,
                                                   int menuItemId,
                                                   final SysObjectListBaseAdapter adapter,
                                                   final BaseUIInterface baseUIInterface) {
        ObjectCreateFragment instance = new ObjectCreateFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putInt(KEY_ITEM_ID, menuItemId);
        instance.setArguments(args);
        instance.sourceAdapter = adapter;
        instance.sourceUiInterface = baseUIInterface;
        return instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleView.setVisibility(View.VISIBLE);
        switch (menuItemId) {
            case R.id.create_cabinet:
                titleView.setText(getString(R.string.new_cabinet));
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.OBJECT_NAME, ""),
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.TITLE, ""),
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.SUBJECT, "")},
                        true);
                break;
            case R.id.create_folder:
                titleView.setText(getString(R.string.new_folder));
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.OBJECT_NAME, ""),
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.TITLE, ""),
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.SUBJECT, "")},
                        true);
                break;
            case R.id.create_document:
                titleView.setText(getString(R.string.new_document));
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.OBJECT_NAME, ""),
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.TITLE, ""),
                                new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.SUBJECT, "")},
                        true);
                downloadButton.setVisibility(View.GONE);
                break;
            case R.id.create_user:
                titleView.setText(getString(R.string.new_user));
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(DctmModelType.USER, DctmPropertyName.USER_NAME, ""),
                                new ObjectDetailItem(DctmModelType.USER, DctmPropertyName.USER_LOGIN_NAME, ""),
                                new ObjectDetailItem(DctmModelType.USER, DctmPropertyName.USER_ADDRESS, ""),
                                new ObjectDetailItem(DctmModelType.USER, DctmPropertyName.USER_SOURCE, "inline password"),
                                new ObjectDetailItem(DctmModelType.USER, DctmPropertyName.USER_PASSWORD, "")},
                        true);
                break;
            case R.id.create_group:
                titleView.setText(getString(R.string.new_group));
                updateAdapterItems(new ObjectDetailItem[]{
                                new ObjectDetailItem(DctmModelType.GROUP, DctmPropertyName.GROUP_NAME, ""),
                                new ObjectDetailItem(DctmModelType.GROUP, DctmPropertyName.DESCRIPTION_GROUP, ""),
                                new ObjectDetailItem(DctmModelType.GROUP, DctmPropertyName.GROUP_DISPLAY_NAME, "")},
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
                    ObjectCreateObservables.checkIn(this, sourceAdapter, sourceUiInterface);
                else if (menuItemId == R.id.create_cabinet || menuItemId == R.id.create_folder || menuItemId == R.id.create_document || menuItemId == R.id.create_user || menuItemId == R.id.create_group)
                    ObjectCreateObservables.create(this, sourceAdapter, sourceUiInterface);
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
}
