/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.adapters.UsersListAdapter;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;
import com.opentext.documentum.rest.sample.android.util.ViewFactory;

import java.util.HashSet;
import java.util.Set;

import butterknife.OnClick;


public class MiniUsersListActivity extends MiniListBaseActivity {
    private static final String SHOW_BUTTONS = "show_buttons_key";
    private static final String GROUP_NAME = "group_name";
    private static final String GROUP_ID = "group_id";

    String groupId;
    boolean showButtons;

    public static Intent newIntent(Context context, boolean showButtons, String groupName, String groupId) {
        Intent intent = new Intent(context, MiniUsersListActivity.class);
        intent.putExtra(SHOW_BUTTONS, showButtons);
        intent.putExtra(GROUP_NAME, groupName);
        intent.putExtra(GROUP_ID, groupId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showButtons = getIntent().getBooleanExtra(SHOW_BUTTONS, false);
        if (!showButtons) okButton.setVisibility(View.GONE);
        else {
            removeButton.setVisibility(View.GONE);
        }
        if (adapters.size() == 0) {
            adapters.add(new UsersListAdapter(this, R.layout.item_cabinetslist, null, null));
            ((ListView) mainComponent).setAdapter(adapters.get(adapters.size() - 1));
        }
        UsersListAdapter lastAdapter = (UsersListAdapter) adapters.get(adapters.size() - 1);

        if (lastAdapter.isEmpty()) {
            groupId = getIntent().getStringExtra(GROUP_ID);
            SysNaviagtionObservables.getEntries(adapters.get(adapters.size() - 1), this, FeedType.USERS, groupId, showButtons);
        } else {
            ((ListView) mainComponent).setAdapter(lastAdapter);
        }
        this.findViewById(R.id.back_button).setOnClickListener(this);
        addStringAndResetToolbar(getIntent().getStringExtra(GROUP_NAME));
    }

    @Override
    View createMainComponent() {
        return ViewFactory.INSTANCE.newListView(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SysObjectListBaseAdapter adapter = adapters.get(adapters.size() - 1);
        if (adapter.containSelected(position))
            adapter.removeSelectedId(position);
        else
            adapter.addSelectId(position);
        // refresh only one row
        View childView = ((ListView) mainComponent).getChildAt(position - ((ListView) mainComponent).getFirstVisiblePosition());
        ((ListView) mainComponent).getAdapter().getView(position, childView, ((ListView) mainComponent)).invalidate();
    }

    @OnClick(R.id.cancel_button)
    void cancel() {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    @OnClick(R.id.ok_button)
    void ok() {
        SysObjectListBaseAdapter adapter = adapters.get(adapters.size() - 1);
        Set<Integer> selected = adapter.getSelectedSet();
        Set<String> ids = new HashSet<>();
        for (int i : selected)
            ids.add(adapter.getItem(i).entry.getId());
        SysNaviagtionObservables.addUserOrGroupToGroups(ids, true, getIntent().getStringExtra(GROUP_ID), this, adapter);
    }

    @OnClick(R.id.remove_button)
    void remove() {
        SysObjectListBaseAdapter adapter = adapters.get(adapters.size() - 1);
        Set<Integer> selected = adapter.getSelectedSet();
        Set<Entry<RestObject>> items = new HashSet<>();
        for (int i : selected)
            items.add(adapter.getItem(i).entry);
        SysNaviagtionObservables.removeUsers(items, this, adapter);
    }

}
