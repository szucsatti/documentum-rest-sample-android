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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.CabinetsListAdapter;
import com.opentext.documentum.rest.sample.android.adapters.GroupsListAdapter;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.DctmModelType;
import com.opentext.documentum.rest.sample.android.enums.DctmObjectType;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;

import java.util.LinkedList;
import java.util.List;


public abstract class SysObjectNavigationBaseFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, View.OnClickListener, BaseUIInterface {
    protected List<SysObjectListBaseAdapter> adapters = new LinkedList<>();


    @Override
    abstract View createMainComponent();

    @Override
    abstract public void onCreateOptionsMenu(Menu menu, MenuInflater inflater);

    @Override
    abstract public boolean onOptionsItemSelected(MenuItem item);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().findViewById(R.id.back_button).setOnClickListener(this);
            if (((TextView) getActivity().findViewById(R.id.drawer_toolbar).findViewById(R.id.toolbar_title)).getText().toString().indexOf("/") == -1)
                getActivity().findViewById(R.id.back_button).setVisibility(View.GONE);
        }
    }

    public SysObjectListBaseAdapter getCurrentAdapter() {
        return this.adapters.get(adapters.size() - 1);
    }

    public void restoreLocation() {
        SysObjectListBaseAdapter lastAdapter = adapters.get(adapters.size() - 1);
        ((ListView) mainComponent).setSelectionFromTop(lastAdapter.getSelect(), lastAdapter.getScrollY());
    }

    void rememberLocation() {
        SysObjectListBaseAdapter lastAdapter = adapters.get(adapters.size() - 1);
        lastAdapter.setScrollY(getScrollY());
        lastAdapter.setSelect(getSelect());
    }

    int getScrollY() {
        View c = ((ListView) mainComponent).getChildAt(0);
        return c.getTop();
    }

    int getSelect() {
        return ((ListView) mainComponent).getFirstVisiblePosition();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE)
            rememberLocation();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getActivity().findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        EntryItem entryItem = (EntryItem) parent.getItemAtPosition(position);
        // todo: add Name type
        switch (entryItem.entry.getContentObject().getName()) {
            case DctmModelType.CABINET:
            case DctmModelType.OBJECT:
                String dm_type = (String) entryItem.entry.getContentObject().getProperties().get("r_object_type");
                if (dm_type.equals("dm_folder") || dm_type.equals("dm_cabinet")) {
                    CabinetsListAdapter newAdapter = new CabinetsListAdapter(getContext(), R.layout.item_cabinetslist, entryItem.entry.getContentObject(), entryItem.entry.getId(), this);
                    adapters.add(newAdapter);
                    ((ListView) mainComponent).setAdapter(newAdapter);
                    ((MainActivity) getActivity()).addStringAndResetToolbar(entryItem.entry.getTitle());
                    SysNaviagtionObservables.getEntries(newAdapter, this, entryItem.entry.getId());
                    getActivity().invalidateOptionsMenu();
                } else if (dm_type.equals("dm_document")) {
                    addObjectDetailFragment(entryItem);
                }
                break;
            case DctmModelType.USER:
                addObjectDetailFragment(entryItem);
                break;
            case DctmModelType.GROUP:
                GroupsListAdapter newAdapter = new GroupsListAdapter(getContext(), R.layout.item_cabinetslist, entryItem.entry.getId(), this);
                adapters.add(newAdapter);
                ((ListView) mainComponent).setAdapter(newAdapter);
                ((MainActivity) getActivity()).addStringAndResetToolbar(entryItem.entry.getTitle());
                SysNaviagtionObservables.getGroups(newAdapter, this, entryItem.entry.getId());
                break;
        }

    }

    public void addObjectDetailFragment(EntryItem entryItem) {
        SysObjectListBaseAdapter adapter = adapters.get(adapters.size() - 1);
        getActivity().findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).addStringAndResetToolbar(entryItem.entry.getTitle());
        String contentType;
        if (entryItem.entry.getContentObject().getName() == null)
            contentType = DctmObjectType.DM_NULL;
        else contentType = entryItem.entry.getContentObject().getName();
        ((MainActivity) getActivity()).attachTmpFragment(ObjectDetailFragment.newInstance(
                entryItem.entry.getId(),
                contentType,
                entryItem.entry.getContentObject(),
                adapter,
                this
        ));
    }

    @Override
    public void onClick(View v) {
        ((MainActivity) getActivity()).removeTopStringAndResetToolbar();
        if (adapters.size() == 1) {
            return;
        }
        adapters.remove(adapters.size() - 1);
        SysObjectListBaseAdapter lastAdapter = adapters.get(adapters.size() - 1);
        ((ListView) mainComponent).setAdapter(lastAdapter);
        if (lastAdapter.isEmpty()) setEmptyBackground();
        else setMainComponentBackground();
        restoreLocation();
        lastAdapter.notifyDataSetChanged();
        getActivity().invalidateOptionsMenu();
    }

}
