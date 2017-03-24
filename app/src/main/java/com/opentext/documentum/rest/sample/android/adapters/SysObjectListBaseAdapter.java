/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.fragments.SysObjectNavigationBaseFragment;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.util.AppDCTMClientBuilder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public abstract class SysObjectListBaseAdapter extends ArrayAdapter<EntryItem> {

    List<Feed<RestObject>> feeds;
    List<EntryItem> entryList;
    Set<Integer> selectedPositions;
    Context context;
    int resourceId;
    String entranceObjectId;
    RestObject entranceObject;
    SysObjectNavigationBaseFragment fragment;
    int scrollY;
    int select;

    public SysObjectListBaseAdapter(Context context, int resource, RestObject entranceObject, String entranceObjectId, SysObjectNavigationBaseFragment fragment) {
        super(context, resource);
        this.resourceId = resource;
        this.context = context;
        this.entryList = new LinkedList<>();
        this.selectedPositions = new HashSet<>();
        this.feeds = new LinkedList<>();
        this.entranceObject = entranceObject;
        this.entranceObjectId = entranceObjectId;
        this.fragment = fragment;
    }

    public SysObjectListBaseAdapter(Context context, int resource, String entranceObjectId, SysObjectNavigationBaseFragment fragment) {
        this(context, resource, null, entranceObjectId, fragment);
    }

    public RestObject getEntranceObject() {
        return this.entranceObject;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public void addSelectId(int pos) {
        this.selectedPositions.add(pos);
    }

    public void removeSelectedId(int pos) {
        if (this.selectedPositions.contains(pos))
            this.selectedPositions.remove(pos);
    }

    public boolean containSelected(int pos) {
        for (int p : this.selectedPositions)
            if (p == pos)
                return true;
        return false;
    }

    public void clearSelected() {
        this.selectedPositions.clear();
    }

    public Set<Integer> getSelectedSet() {
        return this.selectedPositions;
    }

    public void addFeed(Feed<RestObject> feed) {
        if (feed == null)
            return;
        this.feeds.add(feed);
        if (feed.getEntries() != null) {
            for (Entry<RestObject> entry : feed.getEntries()) {
                this.entryList.add(new EntryItem(entry));
            }
        }
    }

    public boolean isEmpty() {
        return entryList.size() == 0;
    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        return entryList.size();
    }

    @Override
    abstract public View getView(final int position, View convertView, ViewGroup parent);

    @Override
    public EntryItem getItem(int position) {
        return entryList.get(position);
    }


    public String regularTime(String time) {
        String result = time.split("\\.")[0];
        String[] tmp = result.split("T");
        return tmp[0] + " " + tmp[1];
    }

    public List<Feed<RestObject>> getAllFeeds() {
        return this.feeds;
    }

    public void replaceEntryItems(List<EntryItem> list) {
        this.entryList.clear();
        for (EntryItem item : list)
            this.entryList.add(item);
    }

    public String getEntranceObjectId() {
        return this.entranceObjectId;
    }

    public void addNextPageFeed() {
        final Feed<RestObject> lastFeed = feeds.get(feeds.size() - 1);
        Observable.create(new Observable.OnSubscribe<Feed>() {
            @Override
            public void call(Subscriber<? super Feed> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                Feed<RestObject> nPage = client.nextPage(lastFeed);
                subscriber.onNext(nPage);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Feed>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, "fail to get more items", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Feed feed) {
                if (feed == null || feed.getEntries() == null || feed.getEntries().isEmpty()) {
                    Toast.makeText(context, "no more items", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "got " + feed.getEntries().size() + " more items", Toast.LENGTH_SHORT).show();
                addFeed(feed);
            }
        });
    }
}
