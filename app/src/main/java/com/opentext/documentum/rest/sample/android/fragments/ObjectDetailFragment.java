/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.DctmObjectType;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;
import com.opentext.documentum.rest.sample.android.observables.ObjectDetailObservables;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;


public class ObjectDetailFragment extends ObjectBaseFragment {
    public static final String KEY_ITEM_ID = "ID";
    public static final String KEY_CONTENT_TYPE = "CONTENT_TYPE";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_REST_OBJECT = "KEY_REST_OBJECT";
    int menuItemId;
    String title;
    String contentType;
    RestObject restObject;
    SysObjectListBaseAdapter sourceAdapter;
    BaseUIInterface sourceUiInterface;

    public ObjectDetailFragment() {
        // Required empty public constructor
    }

    public static ObjectDetailFragment newInstance(String id, String contentType, RestObject restObject,
                                                   final SysObjectListBaseAdapter adapter,
                                                   final BaseUIInterface baseUIInterface) {
        ObjectDetailFragment instance = new ObjectDetailFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putString(KEY_CONTENT_TYPE, contentType);
        instance.setArguments(args);
        instance.setRestObject(restObject);
        instance.sourceAdapter = adapter;
        instance.sourceUiInterface = baseUIInterface;
        return instance;
    }

    public static ObjectDetailFragment newInstance(String id, int menuItemId, String title,
                                                   String contentType, RestObject restObject,
                                                   final SysObjectListBaseAdapter adapter,
                                                   final BaseUIInterface baseUIInterface) {
        ObjectDetailFragment instance = new ObjectDetailFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putInt(KEY_ITEM_ID, menuItemId);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_CONTENT_TYPE, contentType);
        instance.setArguments(args);
        instance.setRestObject(restObject);
        instance.sourceAdapter = adapter;
        instance.sourceUiInterface = baseUIInterface;
        return instance;
    }

    public RestObject getRestObject() {
        return this.restObject;
    }

    public void setRestObject(RestObject restObject) {
        this.restObject = restObject;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(getResources().getString(R.string.properties));
        getActivity().findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).removeTopStringAndResetToolbar();
                ((MainActivity) getActivity()).removeTmpFragment(ObjectDetailFragment.this);
            }
        });
        switch (contentType) {
            case DctmObjectType.DM_DOCUMENT:
                break;
            default:
                setNullContent();
                break;
        }
        if (("document".equals(contentType) || "object".equals(contentType)) && hasContent()) {
            ObjectDetailObservables.loadObjectContent(this);
        }
        ObjectDetailObservables.loadObjectProperties(this);
        if (menuItemId == R.id.check_in_major || menuItemId == R.id.check_in_minor || menuItemId == R.id.check_in_branch)
            ((MainActivity) getActivity()).addStringAndResetToolbar(title);
        else {
            textView.setEnabled(false);
            txtButton.setVisibility(View.GONE);
            fileButton.setVisibility(View.GONE);
        }
    }

    private boolean hasContent() {
        return restObject.getProperties().containsKey(DctmPropertyName.R_CONTENT_SIZE)
                && ((int) restObject.getProperties().get(DctmPropertyName.R_CONTENT_SIZE)) > 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (menuItemId == R.id.check_in_major || menuItemId == R.id.check_in_minor || menuItemId == R.id.check_in_branch)
                    ObjectDetailObservables.checkIn(this);
                else
                    ObjectDetailObservables.updateObject(this, sourceAdapter, sourceUiInterface);
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (("group".equals(contentType) || "user".equals(contentType))
                && !AppCurrentUser.canCreateUserGroup()) {
            MenuItem saveItem = menu.findItem(R.id.ok);
            saveItem.setEnabled(false);
            saveItem.setIcon(getResources().getDrawable(R.drawable.vic_save_g));
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        objectId = args.getString(KEY_ID);
        menuItemId = args.getInt(KEY_ITEM_ID);
        title = args.getString(title);
        contentType = args.getString(KEY_CONTENT_TYPE);
    }

    public int getMenuItemId() {
        return menuItemId;
    }

}
