/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.MiniGroupsListActivity;
import com.opentext.documentum.rest.sample.android.MiniUsersListActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.GroupsListAdapter;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;
import com.opentext.documentum.rest.sample.android.util.ViewFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class GroupsFragment extends SysObjectNavigationBaseFragment {
    private static final int REQUEST_ADD_USERS = 0x1;
    private static final int REQUEST_ADD_GROUPS = 0x2;
    private Menu contextMenu;
    private Menu optionsMenu;

    @Override
    View createMainComponent() {
        final ListView listView = ViewFactory.INSTANCE.newListView(getContext(), this);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            List<String> ids = new LinkedList<String>();
            List<String> names = new LinkedList<String>();
            Set<Entry<RestObject>> entries = new HashSet<>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                View view = listView.getChildAt(position);
                // change color here
                Entry entry = ((EntryItem) listView.getAdapter().getItem(position)).entry;
                if (checked) {
                    ((GroupsListAdapter) listView.getAdapter()).addSelectId(position);
                    ids.add(entry.getId());
                    names.add(entry.getTitle());
                    entries.add(entry);
                } else {
                    ((GroupsListAdapter) listView.getAdapter()).removeSelectedId(position);
                    for (int i = ids.size() - 1; i >= 0; --i) {
                        if (ids.get(i).equals(entry.getId())) {
                            ids.remove(i);
                            names.remove(i);
                            entries.remove(entry);
                        }
                    }
                }
                // refresh only one row
                View childView = listView.getChildAt(position - listView.getFirstVisiblePosition());
                listView.getAdapter().getView(position, childView, listView).invalidate();
                updateContextMenu();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                contextMenu = menu;
                MenuInflater mi = mode.getMenuInflater();
                if (AppCurrentUser.canCreateUserGroup()) {
                    mi.inflate(R.menu.groups_list_context_menu, menu);
                }

                return true;
            }

            public void updateContextMenu() {
                if (contextMenu == null) {
                    return;
                }
                if (adapters.size() > 1) {
                    hideMenuItem(contextMenu, R.id.delete_group_menu);
                } else {
                    hideMenuItem(contextMenu, R.id.remove_group_menu);
                }
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
                switch (item.getItemId()) {
                    case R.id.more_group_menu:
                        return true;
                    case R.id.delete_group_menu:
                        SysNaviagtionObservables.deleteObject(adapters.get(adapters.size() - 1), GroupsFragment.this, idArray);
                        break;
                    case R.id.remove_group_menu:
                        SysNaviagtionObservables.removeUsers(entries, GroupsFragment.this, adapters.get(adapters.size() - 1));
                        break;
                    case R.id.add_users_menu:
                    case R.id.show_users_menu:
                        if (ids.size() != 1) {
                            Toast.makeText(getContext(), "deal with groups once a time", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (item.getItemId() == R.id.add_users_menu) {
                            Intent usersList = MiniUsersListActivity.newIntent(getContext(), true, names.get(0), ids.get(0));
                            startActivityForResult(usersList, REQUEST_ADD_USERS);
                        } else if (item.getItemId() == R.id.show_users_menu) {
                            Intent usersList = MiniUsersListActivity.newIntent(getContext(), false, names.get(0), ids.get(0));
                            startActivity(usersList);
                        }
                        break;
                    case R.id.add_groups_menu:
                        if (ids.size() != 1) {
                            Toast.makeText(getContext(), "deal with groups once a time", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Intent groupsList = MiniGroupsListActivity.newIntent(getContext(), true, names.get(0), ids.get(0));
                        startActivityForResult(groupsList, REQUEST_ADD_GROUPS);
                        break;
                }
                ids.clear();
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ids.clear();
                ((GroupsListAdapter) listView.getAdapter()).clearSelected();
            }
        });
        return listView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_list_normal_menu, menu);
        optionsMenu = menu;
        if (!AppCurrentUser.canCreateUserGroup()) {
            MenuItem createGroupItem = menu.findItem(R.id.create_group);
            createGroupItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GroupsListAdapter adapter = (GroupsListAdapter) adapters.get(adapters.size() - 1);
        switch (item.getItemId()) {
            case R.id.create_group:
                ((MainActivity) getActivity()).attachTmpFragment(
                        ObjectCreateFragment.newInstance(
                                adapter.getEntranceObjectId(),
                                R.id.create_group,
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        if (adapters.size() > 1 && optionsMenu != null) {
            hideMenuItem(optionsMenu, R.id.create_group);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapters.size() == 0) {
            adapters.add(new GroupsListAdapter(getContext(), R.layout.item_cabinetslist, null, this));
            ((ListView) mainComponent).setAdapter(adapters.get(adapters.size() - 1));
        }
        GroupsListAdapter lastAdapter = (GroupsListAdapter) adapters.get(adapters.size() - 1);

        if (lastAdapter.isEmpty())
            SysNaviagtionObservables.getEntries(adapters.get(adapters.size() - 1), this, FeedType.GROUPS, null);
        else {
            ((ListView) mainComponent).setAdapter(lastAdapter);
        }
        getActivity().findViewById(R.id.back_button).setOnClickListener(this);
    }

    private static void hideMenuItem(Menu menu, int id) {
        MenuItem item = menu.findItem(id);
        if (item != null) {
            item.setVisible(false);
        }
    }
}
