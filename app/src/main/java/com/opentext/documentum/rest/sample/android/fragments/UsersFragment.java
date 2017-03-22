/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.MiniGroupsListActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.UsersListAdapter;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;

import java.util.LinkedList;
import java.util.List;


public class UsersFragment extends SysObjectNavigationBaseFragment {
    @Override
    View createMainComponent() {
        final ListView listView = new ListView(getContext());
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(24);
        listView.setDivider(getResources().getDrawable(R.color.pureWhite));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            List<String> ids = new LinkedList<String>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                View view = listView.getChildAt(position);
                // change color here
                if (checked) {
                    ((UsersListAdapter) listView.getAdapter()).addSelectId(position);
                    ids.add(((EntryItem) listView.getAdapter().getItem(position)).entry.getId());
                } else {
                    ((UsersListAdapter) listView.getAdapter()).removeSelectedId(position);
                    for (int i = ids.size() - 1; i >= 0; --i)
                        if (ids.get(i).equals(((EntryItem) listView.getAdapter().getItem(position)).entry.getId()))
                            ids.remove(i);
                }
                // refresh only one row
                View childView = listView.getChildAt(position - listView.getFirstVisiblePosition());
                listView.getAdapter().getView(position, childView, listView).invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater mi = mode.getMenuInflater();
                if (AppCurrentUser.canCreateUserGroup()) {
                    mi.inflate(R.menu.users_list_context_menu, menu);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                String[] idArray = new String[ids.size()];
                for (int i = 0; i < idArray.length; ++i)
                    idArray[i] = ids.get(i);
                // TODO: menu item clicked.
                boolean flag = true;
                switch (item.getItemId()) {
                    case R.id.move_to_group_menu:
                        Intent intent = MiniGroupsListActivity.newIntent(getContext(), idArray);
                        startActivity(intent);
                        break;
                    case R.id.delete_user_menu:
                        SysNaviagtionObservables.deleteObject(adapters.get(adapters.size() - 1), UsersFragment.this, idArray);
                        break;
                }
                if (flag) {
                    ids.clear();
                    mode.finish();
                    return true;
                } else {
                    return true;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ids.clear();
                ((UsersListAdapter) listView.getAdapter()).clearSelected();
            }
        });
        return listView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.users_list_normal_menu, menu);
        if (!AppCurrentUser.canCreateUserGroup()) {
            MenuItem createUserItem = menu.findItem(R.id.create_user);
            createUserItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UsersListAdapter adapter = (UsersListAdapter) adapters.get(adapters.size() - 1);
        switch (item.getItemId()) {
            case R.id.create_user:
                ((MainActivity) getActivity()).attachTmpFragment(ObjectCreateFragment.newInstance(
                        adapter.getEntranceObjectId(),
                        R.id.create_user,
                        adapter,
                        this));
                break;
            case R.id.refresh_menu:
                SysNaviagtionObservables.refresh(adapter, this);
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapters.size() == 0) {
            adapters.add(new UsersListAdapter(getContext(), R.layout.item_cabinetslist, null, this));
            ((ListView) mainComponent).setAdapter(adapters.get(adapters.size() - 1));
        }
        UsersListAdapter lastAdapter = (UsersListAdapter) adapters.get(adapters.size() - 1);

        if (lastAdapter.isEmpty())
            SysNaviagtionObservables.getEntries(adapters.get(adapters.size() - 1), this, FeedType.USERS, null);
        else {
            ((ListView) mainComponent).setAdapter(lastAdapter);
        }
        getActivity().findViewById(R.id.back_button).setOnClickListener(this);
    }
}
