/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.CabinetsListAdapter;
import com.opentext.documentum.rest.sample.android.enums.DctmObjectType;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.observables.SysNaviagtionObservables;
import com.opentext.documentum.rest.sample.android.util.AllowableActionsHelper;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;
import com.opentext.documentum.rest.sample.android.util.MISCHelper;
import com.opentext.documentum.rest.sample.android.util.ThemeResolver;

import java.util.LinkedList;
import java.util.List;


public class CabinetsFragment extends SysObjectNavigationBaseFragment {
    private static final String TAG = "CabinetsFragment";
    private Menu contextMenu;

    @Override
    View createMainComponent() {
        final ListView listView = new ListView(getContext());
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(ThemeResolver.resolve(getContext().getTheme(), R.attr.colorPrimary));
        int[] colors = {0, R.color.textGray, 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(1);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            List<String> ids = new LinkedList<String>();
            List<RestObject> objects = new LinkedList<RestObject>();
            List<String> contentTypes = new LinkedList<String>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                View view = listView.getChildAt(position);
                // change color here
                if (checked) {
                    ((CabinetsListAdapter) listView.getAdapter()).addSelectId(position);
                    Entry<RestObject> entry = ((EntryItem) listView.getAdapter().getItem(position)).entry;
                    ids.add(entry.getId());
                    objects.add(entry.getContentObject());
                    String contentType;
                    if (entry.getContentObject().getType() == null)
                        contentType = DctmObjectType.DM_NULL;
                    else contentType = entry.getContentObject().getType();
                    contentTypes.add(contentType);
                    SysNaviagtionObservables.refreshObject(getCurrentAdapter(), CabinetsFragment.this, entry.getContentObject());
                } else {
                    ((CabinetsListAdapter) listView.getAdapter()).removeSelectedId(position);
                    for (int i = ids.size() - 1; i >= 0; --i)
                        if (ids.get(i).equals(((EntryItem) listView.getAdapter().getItem(position)).entry.getId())) {
                            ids.remove(i);
                            objects.remove(i);
                            contentTypes.remove(i);
                        }
                }
                // refresh only one row
                View childView = listView.getChildAt(position - listView.getFirstVisiblePosition());
                listView.getAdapter().getView(position, childView, listView).invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater mi = mode.getMenuInflater();
                mi.inflate(R.menu.objects_list_context_menu, menu);
                contextMenu = menu;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                CabinetsListAdapter adapter = (CabinetsListAdapter) adapters.get(adapters.size() - 1);
                String[] idArray = new String[ids.size()];
                for (int i = 0; i < idArray.length; ++i)
                    idArray[i] = ids.get(i);
                // TODO: menu item clicked.
                boolean flag = true;
                if (R.id.dele_menu == item.getItemId()) {
                    SysNaviagtionObservables.deleteObject(adapters.get(adapters.size() - 1), CabinetsFragment.this, idArray);
                } else if (R.id.more_menu == item.getItemId()) {
                    return true;
                } else {
                    switch (item.getItemId()) {
                        case R.id.copy_menu:
                            MISCHelper.setTmpIds(idArray);
                            MISCHelper.setMoveFromAdapter(null);
                            getActivity().invalidateOptionsMenu();
                            break;
                        case R.id.move_menu:
                            MISCHelper.setTmpIds(idArray);
                            MISCHelper.setMoveFromAdapter((CabinetsListAdapter) listView.getAdapter());
                            getActivity().invalidateOptionsMenu();
                            break;
                        case R.id.check_out_menu:
                            SysNaviagtionObservables.checkOut(CabinetsFragment.this, idArray, adapters.get(adapters.size() - 1));
                            break;
                        case R.id.cancel_check_out:
                            SysNaviagtionObservables.cancelCheckOut(CabinetsFragment.this, idArray, adapters.get(adapters.size() - 1));
                            break;
                        case R.id.check_in_major:
                        case R.id.check_in_minor:
                        case R.id.check_in_branch:
                            ((MainActivity) getActivity()).attachTmpFragment(ObjectDetailFragment.newInstance(
                                    idArray[0],
                                    contentTypes.get(0),
                                    objects.get(0),
                                    item.getItemId(),
                                    adapter,
                                    CabinetsFragment.this
                            ));
                            break;
                    }
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
                ((CabinetsListAdapter) listView.getAdapter()).clearSelected();
            }
        });
        return listView;
    }

    //todo: need to address this rest api limitation -- versioning links are not returned in collection, so we have to re-fetch the object
    public void updateContextMenu(RestObject contentObject) {
        if (!AllowableActionsHelper.canCheckout(contentObject)) {
            hideMenuItem(contextMenu, R.id.check_out_menu);
        }
        if (!AllowableActionsHelper.canCancelCheckout(contentObject)) {
            hideMenuItem(contextMenu, R.id.cancel_check_out);
        }
        if (!AllowableActionsHelper.canCheckinAsMajor(contentObject)) {
            hideMenuItem(contextMenu, R.id.check_in_major);
        }
        if (!AllowableActionsHelper.canCheckinAsMinor(contentObject)) {
            hideMenuItem(contextMenu, R.id.check_in_minor);
        }
        if (!AllowableActionsHelper.canCheckinAsBranch(contentObject)) {
            hideMenuItem(contextMenu, R.id.check_in_branch);
        }
        if (!AllowableActionsHelper.canCopyFrom(contentObject)) {
            hideMenuItem(contextMenu, R.id.copy_menu);
        }
        if (!AllowableActionsHelper.canMoveFrom(contentObject)) {
            hideMenuItem(contextMenu, R.id.move_menu);
        }
        if (!AllowableActionsHelper.canDelete(contentObject)) {
            hideMenuItem(contextMenu, R.id.dele_menu);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.objects_list_normal_menu, menu);

        if (onCabinetsView()) {
            if (!AppCurrentUser.canCreateCabinet()) {
                hideMenuItem(menu, R.id.create_object);
            }
            hideMenuItem(menu, R.id.create_document);
            hideMenuItem(menu, R.id.create_folder);
            hideMenuItem(menu, R.id.move_here_menu);
        } else {
            hideMenuItem(menu, R.id.create_cabinet);
            if (!AllowableActionsHelper.canCopyTo(getEntranceFolder())) {
                hideMenuItem(menu, R.id.copy_here_menu);
            }
            if (!AllowableActionsHelper.canMoveTo(getEntranceFolder())
                    || (this.adapters.size() > 0 && getCurrentAdapter().equals(MISCHelper.getMoveFromAdapter()))) {
                hideMenuItem(menu, R.id.move_here_menu);
            }
        }
        if (MISCHelper.getTmpIds() == null) {
            hideMenuItem(menu, R.id.move_here_menu);
            hideMenuItem(menu, R.id.copy_here_menu);
            hideMenuItem(menu, R.id.cancel_copy_menu);
            hideMenuItem(menu, R.id.cancel_move_menu);
        } else if (MISCHelper.getMoveFromAdapter() == null) {
            hideMenuItem(menu, R.id.move_here_menu);
            hideMenuItem(menu, R.id.cancel_move_menu);
        } else {
            hideMenuItem(menu, R.id.copy_here_menu);
            hideMenuItem(menu, R.id.cancel_copy_menu);
        }
    }

    private void hideMenuItem(Menu menu, int id) {
        MenuItem item = menu.findItem(id);
        if (item != null) {
            item.setVisible(false);
        }
    }

    private boolean onCabinetsView() {
        return this.adapters.size() == 1;
    }

    private RestObject getEntranceFolder() {
        return this.getCurrentAdapter().getEntranceObject();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CabinetsListAdapter adapter = (CabinetsListAdapter) adapters.get(adapters.size() - 1);
        switch (item.getItemId()) {
            case R.id.create_cabinet:
                ((MainActivity) getActivity()).attachTmpFragment(ObjectCreateFragment.newInstance(
                        adapter.getEntranceObjectId(),
                        R.id.create_cabinet,
                        adapter,
                        this));
                break;
            case R.id.create_folder:
                ((MainActivity) getActivity()).attachTmpFragment(ObjectCreateFragment.newInstance(
                        adapter.getEntranceObjectId(),
                        R.id.create_folder,
                        adapter,
                        this));
                break;
            case R.id.create_document:
                ((MainActivity) getActivity()).attachTmpFragment(ObjectCreateFragment.newInstance(
                        adapter.getEntranceObjectId(),
                        R.id.create_document,
                        adapter,
                        this));
                break;
            case R.id.copy_here_menu:
                SysNaviagtionObservables.copyObject(adapter, this);
                break;
            case R.id.move_here_menu:
                SysNaviagtionObservables.move(adapter, this);
                break;
            case R.id.cancel_copy_menu:
                MISCHelper.setTmpIds(null);
                CabinetsFragment.this.getActivity().invalidateOptionsMenu();
                break;
            case R.id.cancel_move_menu:
                MISCHelper.setTmpIds(null);
                MISCHelper.setMoveFromAdapter(null);
                CabinetsFragment.this.getActivity().invalidateOptionsMenu();
                break;
            case R.id.search_menu:
                //todo
                break;
            case R.id.refresh_menu:
                Log.d(TAG, "refresh_menu");
                SysNaviagtionObservables.refresh(adapter, this);
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // here should get a first cabinets feed
        if (adapters.size() == 0) {
            adapters.add(new CabinetsListAdapter(getContext(), R.layout.item_cabinetslist, null, null, this));
            ((ListView) mainComponent).setAdapter(adapters.get(adapters.size() - 1));
        }
        CabinetsListAdapter lastAdapter = (CabinetsListAdapter) adapters.get(adapters.size() - 1);

        if (lastAdapter.isEmpty())
            SysNaviagtionObservables.getEntries(adapters.get(adapters.size() - 1), this, FeedType.CABINETS, null);
        else {
            ((ListView) mainComponent).setAdapter(lastAdapter);
        }
        getActivity().findViewById(R.id.back_button).setOnClickListener(this);

    }
}
