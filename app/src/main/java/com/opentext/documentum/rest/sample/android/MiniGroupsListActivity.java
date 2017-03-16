/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.opentext.documentum.rest.sample.android.adapters.GroupsListAdapter;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;

import java.util.HashSet;
import java.util.Set;

import butterknife.OnClick;


public class MiniGroupsListActivity extends MiniListBaseActivity {
    private static final String IDS_KEY = "ids_key";
    private static final String SHOW_BUTTONS = "show_buttons_key";
    private static final String GROUP_NAME = "group_name";
    private static final String GROUP_ID = "group_id";

    private String[] ids;

    public static Intent newIntent(Context context, String[] ids) {
        Intent intent = new Intent(context, MiniGroupsListActivity.class);
        intent.putExtra(IDS_KEY, ids);
        return intent;
    }

    public static Intent newIntent(Context context, boolean showButtons, String groupName, String groupId) {
        Intent intent = new Intent(context, MiniGroupsListActivity.class);
        intent.putExtra(SHOW_BUTTONS, showButtons);
        intent.putExtra(GROUP_NAME, groupName);
        intent.putExtra(GROUP_ID, groupId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeButton.setVisibility(View.GONE);
        ids = getIntent().getStringArrayExtra(IDS_KEY);

        if (adapters.size() == 0) {
            adapters.add(new GroupsListAdapter(this, R.layout.item_cabinetslist, null, null));
            ((ListView) mainComponent).setAdapter(adapters.get(adapters.size() - 1));
        }
        GroupsListAdapter lastAdapter = (GroupsListAdapter) adapters.get(adapters.size() - 1);

        if (lastAdapter.isEmpty())
            SysNaviagtionObservables.getEntries(adapters.get(adapters.size() - 1), this, FeedType.GROUPS, null);
        else {
            ((ListView) mainComponent).setAdapter(lastAdapter);
        }
        this.findViewById(R.id.back_button).setOnClickListener(this);
        addStringAndResetToolbar("Groups");
    }

    @Override
    View createMainComponent() {
        final ListView listView = new ListView(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(24);
        listView.setDivider(getResources().getDrawable(R.color.pureWhite));
        return listView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        this.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
//        EntryItem entryItem = (EntryItem) parent.getItemAtPosition(position);
//        GroupsListAdapter newAdapter = new GroupsListAdapter(this, R.layout.item_cabinetslist, entryItem.entry.getId(), null);
//        adapters.add(newAdapter);
//        ((ListView) mainComponent).setAdapter(newAdapter);
//        this.addStringAndResetToolbar(entryItem.entry.getTitle());
//        SysNaviagtionObservables.getGroups(newAdapter, this, entryItem.entry.getId());

        SysObjectListBaseAdapter adapter = adapters.get(adapters.size() - 1);
        if (adapter.containsSeleted(position))
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
        Set<String> userIds = new HashSet<>();
        if (adapter instanceof GroupsListAdapter) {
            Set<Integer> selected = adapter.getSelectedSet();
            for (int i : selected) {
                userIds.add(adapter.getItem(i).entry.getId());
            }
            SysNaviagtionObservables.addUserOrGroupToGroups(userIds, false, getIntent().getStringExtra(GROUP_ID), this, adapter);
        } else {
            for (String id : ids)
                userIds.add(id);
            SysNaviagtionObservables.addUserOrGroupToGroups(userIds, true, adapter.getEntranceObjectId(), this, null);
        }
    }

}
