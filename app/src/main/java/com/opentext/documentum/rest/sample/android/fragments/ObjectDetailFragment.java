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
import com.opentext.documentum.rest.sample.android.enums.DctmModelType;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;
import com.opentext.documentum.rest.sample.android.items.ObjectDetailItem;
import com.opentext.documentum.rest.sample.android.observables.ObjectDetailObservables;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;
import com.opentext.documentum.rest.sample.android.util.MimeIconHelper;
import com.opentext.documentum.rest.sample.android.util.RestObjectUtil;


public class ObjectDetailFragment extends ObjectBaseFragment {
    public static final String KEY_ITEM_ID = "ID";
    public static final String KEY_CONTENT_TYPE = "CONTENT_TYPE";
    int menuItemId;
    String contentType;
    RestObject restObject;
    SysObjectListBaseAdapter sourceAdapter;
    BaseUIInterface sourceUiInterface;

    public ObjectDetailFragment() {
        // Required empty public constructor
    }

    public static ObjectDetailFragment newInstance(String id, String contentType, RestObject restObject,
                                                   final int menuItemId,
                                                   final SysObjectListBaseAdapter adapter,
                                                   final BaseUIInterface baseUIInterface) {
        ObjectDetailFragment instance = new ObjectDetailFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putString(KEY_CONTENT_TYPE, contentType);
        instance.setArguments(args);
        instance.restObject = restObject;
        instance.menuItemId = menuItemId;
        instance.sourceAdapter = adapter;
        instance.sourceUiInterface = baseUIInterface;
        return instance;
    }

    public static ObjectDetailFragment newInstance(String id, String contentType, RestObject restObject,
                                                   final SysObjectListBaseAdapter adapter,
                                                   final BaseUIInterface baseUIInterface) {
        return newInstance(id, contentType, restObject, 0, adapter, baseUIInterface);
    }

    public RestObject getRestObject() {
        return this.restObject;
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
        String name = RestObjectUtil.getString(restObject, DctmPropertyName.OBJECT_NAME);
        if (menuItemId == R.id.check_in_major || menuItemId == R.id.check_in_minor || menuItemId == R.id.check_in_branch) {
            ((MainActivity) getActivity()).addStringAndResetToolbar(name);
            updateAdapterItems(new ObjectDetailItem[]{
                            new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.OBJECT_NAME, name),
                            new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.TITLE, RestObjectUtil.getString(restObject, DctmPropertyName.TITLE)),
                            new ObjectDetailItem(DctmModelType.OBJECT, DctmPropertyName.SUBJECT, RestObjectUtil.getString(restObject, DctmPropertyName.SUBJECT))},
                    true);
            titleView.setText(R.string.new_document);
            contentTitleView.setVisibility(View.VISIBLE);
            contentTitleView.setText(getString(R.string.new_content));
            txtButton.setVisibility(View.VISIBLE);
            fileButton.setVisibility(View.VISIBLE);
        }
        else {
            ObjectDetailObservables.loadObjectProperties(this);
            if (!DctmModelType.DOCUMENT.equals(contentType)
                    || !hasContent()
                    || !(MimeIconHelper.isTxt(name) || MimeIconHelper.isImage(name))) {
                setNullContent();
            } else {
                textView.setEnabled(false);
                txtButton.setVisibility(View.GONE);
                fileButton.setVisibility(View.GONE);
                ObjectDetailObservables.loadObjectContent(this);
            }
        }
    }

    private boolean hasContent() {
        return RestObjectUtil.getInt(restObject, DctmPropertyName.R_CONTENT_SIZE) > 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (menuItemId == R.id.check_in_major || menuItemId == R.id.check_in_minor || menuItemId == R.id.check_in_branch)
                    ObjectDetailObservables.checkIn(this, sourceAdapter, sourceUiInterface);
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
            saveItem.setVisible(false);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        objectId = args.getString(KEY_ID);
        menuItemId = args.getInt(KEY_ITEM_ID);
        contentType = args.getString(KEY_CONTENT_TYPE);
    }

    public int getMenuItemId() {
        return menuItemId;
    }

}
