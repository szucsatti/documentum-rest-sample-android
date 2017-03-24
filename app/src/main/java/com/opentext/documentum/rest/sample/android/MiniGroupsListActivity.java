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

import com.opentext.documentum.rest.sample.android.adapters.GroupsListAdapter;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;
import com.opentext.documentum.rest.sample.android.util.ViewFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.OnClick;


public class MiniGroupsListActivity extends MiniListBaseActivity {
    private static final String IDS_KEY = "ids_key";
    private static final String SHOW_BUTTONS = "show_buttons_key";
    private static final String GROUP_NAME = "group_name";
    private static final String GROUP_ID = "group_id";
    private static final String FROM_GROUP = "from_group";

    private String[] ids;

    public static Intent newIntent(Context context, String[] ids) {
        Intent intent = new Intent(context, MiniGroupsListActivity.class);
        intent.putExtra(IDS_KEY, ids);
        intent.putExtra(FROM_GROUP, false);
        return intent;
    }

    public static Intent newIntent(Context context, boolean showButtons, String groupName, String groupId) {
        Intent intent = new Intent(context, MiniGroupsListActivity.class);
        intent.putExtra(SHOW_BUTTONS, showButtons);
        intent.putExtra(GROUP_NAME, groupName);
        intent.putExtra(GROUP_ID, groupId);
        intent.putExtra(FROM_GROUP, true);
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
        Set<String> userIds = new HashSet<>();
        boolean fromGroup = getIntent().getBooleanExtra(FROM_GROUP, true);
        if (fromGroup) {
            Set<Integer> selected = adapter.getSelectedSet();
            for (int i : selected) {
                userIds.add(adapter.getItem(i).entry.getId());
            }
            List<String> groupIds = Arrays.asList(getIntent().getStringExtra(GROUP_ID));
            SysNaviagtionObservables.addUserOrGroupToGroups(userIds, false, groupIds, this, adapter);
        } else {
            for (String id : ids)
                userIds.add(id);
            List<String> groupIds = new ArrayList<>();
            Set<Integer> selected = adapter.getSelectedSet();
            for (int i : selected) {
                groupIds.add(adapter.getItem(i).entry.getId());
            }
            SysNaviagtionObservables.addUserOrGroupToGroups(userIds, true, groupIds, this, null);
        }
        this.finish();
    }

}
