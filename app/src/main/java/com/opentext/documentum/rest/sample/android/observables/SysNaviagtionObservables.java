/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.observables;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.FolderLink;
import com.emc.documentum.rest.client.sample.model.LinkRelation;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.builder.BatchBuilder;
import com.emc.documentum.rest.client.sample.model.plain.PlainFolderLink;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.FeedType;
import com.opentext.documentum.rest.sample.android.fragments.BaseFragment;
import com.opentext.documentum.rest.sample.android.fragments.BaseUIInterface;
import com.opentext.documentum.rest.sample.android.fragments.CabinetsFragment;
import com.opentext.documentum.rest.sample.android.fragments.SysObjectNavigationBaseFragment;
import com.opentext.documentum.rest.sample.android.items.EntryItem;
import com.opentext.documentum.rest.sample.android.util.AppDCTMClientBuilder;
import com.opentext.documentum.rest.sample.android.util.MISCHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.opentext.documentum.rest.sample.android.util.TypeInfoHelper.DOCUMENT_ATTR_LIST;
import static com.opentext.documentum.rest.sample.android.util.TypeInfoHelper.FOLDER_ATTR_LIST;
import static com.opentext.documentum.rest.sample.android.util.TypeInfoHelper.GROUP_ATTR_LIST;
import static com.opentext.documentum.rest.sample.android.util.TypeInfoHelper.USER_ATTR_LIST;


public class SysNaviagtionObservables {
    private static final String TAG = "SysNaviagtionObserv";

    public static void getEntries(final SysObjectListBaseAdapter adapter, final BaseFragment fragment, final String id) {

        fragment.setLoadingBackground();
        Observable.create(new Observable.OnSubscribe<SysObjectListBaseAdapter>() {
            @Override
            public void call(Subscriber<? super SysObjectListBaseAdapter> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                Feed<RestObject> feed;
                if (id.equals("")) {
                    feed = null;
                    subscriber.onError(new Exception("id is blank"));
                } else {
                    feed = client.getFolders(client.getObject(id), "inline", "true", "view", FOLDER_ATTR_LIST);
                    adapter.addFeed(feed);
                    feed = client.getDocuments(client.getObject(id), "inline", "true", "view", DOCUMENT_ATTR_LIST);
                    adapter.addFeed(feed);
                }
                subscriber.onNext(adapter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SysObjectListBaseAdapter>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fragment.setErrorBackground();
                Log.d(TAG, throwableToString(e));
            }

            @Override
            public void onNext(SysObjectListBaseAdapter adapter) {
                if (adapter.isEmpty()) {
                    fragment.setEmptyBackground();
                    return;
                }
                fragment.setMainComponentBackground();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void getGroups(final SysObjectListBaseAdapter adapter, final BaseUIInterface fragment, final String id) {
        fragment.setLoadingBackground();
        Observable.create(new Observable.OnSubscribe<SysObjectListBaseAdapter>() {
            @Override
            public void call(Subscriber<? super SysObjectListBaseAdapter> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                Feed<RestObject> feed;
                if (id.equals("")) {
                    feed = null;
                    subscriber.onError(new Exception("id is blank"));
                } else {
                    feed = client.getGroups(client.getObject(id), "inline", "true", "view", GROUP_ATTR_LIST);
                }
                adapter.addFeed(feed);
                subscriber.onNext(adapter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SysObjectListBaseAdapter>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fragment.setErrorBackground();
                Log.d(TAG, throwableToString(e));
            }

            @Override
            public void onNext(SysObjectListBaseAdapter adapter) {
                if (adapter.isEmpty()) {
                    fragment.setEmptyBackground();
                    return;
                }
                fragment.setMainComponentBackground();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void getEntries(final SysObjectListBaseAdapter adapter, final BaseUIInterface fragment, final FeedType type, final String id) {
        final boolean showAll = true;
        getEntries(adapter, fragment, type, id, showAll);
    }

    public static void getEntries(final SysObjectListBaseAdapter adapter, final BaseUIInterface fragment, final FeedType type, final String id, final boolean showAll) {

        fragment.setLoadingBackground();
        Observable.create(new Observable.OnSubscribe<SysObjectListBaseAdapter>() {
            @Override
            public void call(Subscriber<? super SysObjectListBaseAdapter> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                Feed<RestObject> feed = null;
                if (showAll || id == null)
                    switch (type) {
                        case USERS:
                            feed = client.getUsers("inline", "true", "view", USER_ATTR_LIST);
                            break;
                        case GROUPS:
                            feed = client.getGroups("inline", "true", "view", GROUP_ATTR_LIST);
                            break;
                        case CABINETS:
                        default:
                            feed = client.getCabinets("inline", "true", "view", FOLDER_ATTR_LIST);
                            break;
                    }
                else {
                    switch (type) {
                        case USERS:
                            feed = client.getUsers(client.getGroup(id), "inline", "true", "view", USER_ATTR_LIST);
                            break;
                    }
                }
                adapter.addFeed(feed);
                subscriber.onNext(adapter);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SysObjectListBaseAdapter>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fragment.setErrorBackground();
                Log.d(TAG, throwableToString(e));
            }

            @Override
            public void onNext(SysObjectListBaseAdapter adapter) {
                if (adapter.isEmpty()) {
                    fragment.setEmptyBackground();
                    return;
                }
                fragment.setMainComponentBackground();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void deleteObject(final SysObjectListBaseAdapter adapter, final BaseUIInterface baseUIInterface, final String... ids) {
        final List<String> failIds = new LinkedList<>();
        baseUIInterface.enableLoadingBackground();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder batchBuilder = BatchBuilder.builder(client);
                for (String singleId : ids)
                    batchBuilder.operation().delete(client.getObject(singleId));
                try {
                    client.createBatch(batchBuilder.build());
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failIds.add("");
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                toastOnError(baseUIInterface, "delete");
            }

            @Override
            public void onNext(Object o) {
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                if (failIds.size() == 0)
                    toastSuccess(baseUIInterface, "delete");
                else {
                    //TODO: add log feature
                    toastFailed(baseUIInterface, "delete");
                }
            }
        });
    }

    public static void refreshObject(final SysObjectListBaseAdapter adapter, final BaseUIInterface baseUIInterface, final RestObject object) {
        baseUIInterface.enableLoadingBackground();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                //todo: not always needed
                DCTMRestClient client = AppDCTMClientBuilder.build();
                RestObject refreshed = client.get(object);
                subscriber.onNext(refreshed);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                toastOnError(baseUIInterface, "delete");
            }

            @Override
            public void onNext(Object o) {
                ((CabinetsFragment) baseUIInterface).updateContextMenu((RestObject) o);
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
            }
        });
    }

    public static void copyObject(final SysObjectListBaseAdapter adapter, final BaseUIInterface fragment) {
        fragment.enableLoadingBackground();
        final List<String> failIds = new LinkedList<>();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder batchBuilder = BatchBuilder.builder(client);
                for (String id : MISCHelper.getTmpIds())
                    batchBuilder.operation().createObject(client.getObject(adapter.getEntranceObjectId()), LinkRelation.OBJECTS, new PlainRestObject(client.getObject(id).self()));
                try {
                    client.createBatch(batchBuilder.build());
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failIds.add("");
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                SysNaviagtionObservables.refresh(adapter, fragment);
                fragment.disableLoadingBackground();
                toastOnError(fragment, "copy");
            }

            @Override
            public void onNext(Object o) {
                SysNaviagtionObservables.refresh(adapter, fragment);
                fragment.disableLoadingBackground();
                MISCHelper.setTmpIds(null);
                if (failIds.size() == 0)
                    toastSuccess(fragment, "copy");
                else {
                    //TODO: add log feature
                    toastFailed(fragment, "copy");
                }
                if (fragment instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) fragment).getActivity().invalidateOptionsMenu();
                else if (fragment instanceof AppCompatActivity)
                    ((AppCompatActivity) fragment).invalidateOptionsMenu();
            }
        });
    }

    public static void move(final SysObjectListBaseAdapter adapter, final BaseUIInterface baseUIInterface) {
        baseUIInterface.enableLoadingBackground();
        final List<String> failIds = new LinkedList<>();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder batchBuilder = BatchBuilder.builder(client);
                final String fromId = client.getObject(MISCHelper.getMoveFromAdapter().getEntranceObjectId()).getObjectId();
                try {
                    for (String id : MISCHelper.getTmpIds()) {
                        RestObject objectToBeMoved = client.getObject(id);
                        RestObject dest = client.getObject(adapter.getEntranceObjectId());
                        Feed<FolderLink> parentFolderLinks = client.getFolderLinks(objectToBeMoved, LinkRelation.PARENT_LINKS);
                        FolderLink parentLinkToBeMove = client.getFolderLink(parentFolderLinks.getEntries().get(0).getContentSrc());
                        if (parentLinkToBeMove.getChildId().equals(objectToBeMoved.getObjectId()) && parentLinkToBeMove.getParentId().equals(fromId)) {
                            batchBuilder.operation().move(parentLinkToBeMove, new PlainFolderLink(dest.self()));
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failIds.add("");
                }
                if (failIds.size() != 0)
                    subscriber.onNext(null);
                else {
                    try {
                        client.createBatch(batchBuilder.build());
                    } catch (Exception e) {
                        //todo: error handling
                        Log.d(TAG, throwableToString(e));
                        failIds.add("");
                    }
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                SysNaviagtionObservables.refresh(MISCHelper.getMoveFromAdapter(), baseUIInterface);
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                toastOnError(baseUIInterface, "move");
                MISCHelper.setTmpIds(null);
                MISCHelper.setMoveFromAdapter(null);
                if (baseUIInterface instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) baseUIInterface).getActivity().invalidateOptionsMenu();
                else if (baseUIInterface instanceof AppCompatActivity)
                    ((AppCompatActivity) baseUIInterface).invalidateOptionsMenu();
            }

            @Override
            public void onNext(Object o) {
                SysNaviagtionObservables.refresh(MISCHelper.getMoveFromAdapter(), baseUIInterface);
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                if (failIds.size() == 0)
                    toastSuccess(baseUIInterface, "move");
                else {
                    //TODO: add log feature
                    toastFailed(baseUIInterface, "move");
                }
                MISCHelper.setTmpIds(null);
                MISCHelper.setMoveFromAdapter(null);
                if (baseUIInterface instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) baseUIInterface).getActivity().invalidateOptionsMenu();
                else if (baseUIInterface instanceof AppCompatActivity)
                    ((AppCompatActivity) baseUIInterface).invalidateOptionsMenu();
            }
        });
    }

    public static void addUserOrGroupToGroups(final Set<String> userIds, final boolean users, final String groupId, final BaseUIInterface baseUIInterface, final SysObjectListBaseAdapter adapter) {
        baseUIInterface.enableLoadingBackground();
        final List<String> failIds = new LinkedList<>();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder batchBuilder = BatchBuilder.builder(client);
                RestObject group = client.getGroup(groupId);
                for (String id : userIds)
                    if (users) {
                        batchBuilder.operation().addUserToGroup(group, client.getUser(id));
                    } else {
                        batchBuilder.operation().addGroupToGroup(group, client.getGroup(id));
                    }
                try {
                    client.createBatch(batchBuilder.build());
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failIds.add("");
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (adapter != null)
                    SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                toastOnError(baseUIInterface, "move");
                if (baseUIInterface instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) baseUIInterface).getActivity().invalidateOptionsMenu();
                else if (baseUIInterface instanceof AppCompatActivity)
                    ((AppCompatActivity) baseUIInterface).invalidateOptionsMenu();
            }

            @Override
            public void onNext(Object o) {
                if (adapter != null)
                    SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                if (failIds.size() == 0) {
                    toastSuccess(baseUIInterface, "move");
                    ((AppCompatActivity) baseUIInterface).finish();
                } else {
                    //TODO: add log feature
                    toastFailed(baseUIInterface, "move");
                }
                if (baseUIInterface instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) baseUIInterface).getActivity().invalidateOptionsMenu();
                else if (baseUIInterface instanceof AppCompatActivity)
                    ((AppCompatActivity) baseUIInterface).invalidateOptionsMenu();
            }
        });
    }

    public static void removeUsers(final Set<Entry<RestObject>> userIds, final BaseUIInterface baseUIInterface, final SysObjectListBaseAdapter adapter) {
        baseUIInterface.enableLoadingBackground();
        final List<String> failIds = new LinkedList<>();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder batchBuilder = BatchBuilder.builder(client);
                for (Entry entry : userIds)
                    batchBuilder.operation().delete(entry);
                try {
                    client.createBatch(batchBuilder.build());
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failIds.add("");
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                toastOnError(baseUIInterface, "move");
                MISCHelper.setTmpIds(null);
                MISCHelper.setMoveFromAdapter(null);
                if (baseUIInterface instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) baseUIInterface).getActivity().invalidateOptionsMenu();
                else if (baseUIInterface instanceof AppCompatActivity)
                    ((AppCompatActivity) baseUIInterface).invalidateOptionsMenu();
            }

            @Override
            public void onNext(Object o) {
                SysNaviagtionObservables.refresh(MISCHelper.getMoveFromAdapter(), baseUIInterface);
                SysNaviagtionObservables.refresh(adapter, baseUIInterface);
                baseUIInterface.disableLoadingBackground();
                if (failIds.size() == 0)
                    toastSuccess(baseUIInterface, "move");
                else {
                    //TODO: add log feature
                    toastFailed(baseUIInterface, "move");
                }
                MISCHelper.setTmpIds(null);
                MISCHelper.setMoveFromAdapter(null);
                if (baseUIInterface instanceof Fragment)
                    ((SysObjectNavigationBaseFragment) baseUIInterface).getActivity().invalidateOptionsMenu();
                else if (baseUIInterface instanceof AppCompatActivity)
                    ((AppCompatActivity) baseUIInterface).invalidateOptionsMenu();
            }
        });
    }

    public static void refresh(final SysObjectListBaseAdapter adapter, final BaseUIInterface baseUIInterface) {
        final List<Feed<RestObject>> feeds = adapter.getAllFeeds();
        final List<EntryItem> newItems = new LinkedList<>();
        final List<Object> dummy = new LinkedList<>();
        baseUIInterface.enableLoadingBackground();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                for (Feed<RestObject> feed : feeds) {
                    //TODO: TEST RIGHT
                    try {
                        Feed<RestObject> tmpFeed = client.get(feed);
                        if (tmpFeed.getEntries() != null)
                            for (Entry<RestObject> entry : tmpFeed.getEntries())
                                newItems.add(new EntryItem(entry));
                    } catch (Exception e) {
                        dummy.add(new Object());
                        Log.d(TAG, throwableToString(e));
                    }
                }
                if (newItems.size() != 0 || dummy.size() == 0)
                    adapter.replaceEntryItems(newItems);
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                adapter.notifyDataSetChanged();
                baseUIInterface.disableLoadingBackground();
                if (adapter.isEmpty())
                    baseUIInterface.setEmptyBackground();
                else
                    baseUIInterface.setMainComponentBackground();
                baseUIInterface.restoreLocation();
                toastOnError(baseUIInterface, "refresh");
            }

            @Override
            public void onNext(Object o) {
                // TODO : need to scroll to former?
                adapter.notifyDataSetChanged();
                baseUIInterface.disableLoadingBackground();
                if (adapter.isEmpty())
                    baseUIInterface.setEmptyBackground();
                else
                    baseUIInterface.setMainComponentBackground();
                baseUIInterface.restoreLocation();
                if (dummy.size() == 0)
                    toastSuccess(baseUIInterface, "refresh");
                else {
                    //TODO: add log feature
                    toastFailed(baseUIInterface, "refresh");
                }
            }
        });
    }

    public static void checkOut(final CabinetsFragment fragment, final String[] idArray, final SysObjectListBaseAdapter adapter) {
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> failure = new LinkedList<>();
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder batchBuilder = BatchBuilder.builder(client);
                for (String id : idArray)
                    batchBuilder.operation().checkout(client.getObject(id));
                try {
                    client.createBatch(batchBuilder.build());
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failure.add("");
                }
                subscriber.onNext(failure);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                Toast.makeText(fragment.getContext(), "checkout failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<String> strings) {
                SysNaviagtionObservables.refresh(adapter, fragment);
                fragment.getActivity().invalidateOptionsMenu();
                if (strings.size() == 0)
                    Toast.makeText(fragment.getContext(), "checkout succeeded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(fragment.getContext(), "checkout failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void cancelCheckOut(final CabinetsFragment fragment, final String[] idArray, final SysObjectListBaseAdapter adapter) {
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> failure = new LinkedList<>();
                DCTMRestClient client = AppDCTMClientBuilder.build();
                BatchBuilder builder = BatchBuilder.builder(client);
                for (String id : idArray)
                    builder.operation().cancelCheckout(client.getObject(id));
                try {
                    client.createBatch(builder.build());
                } catch (Exception e) {
                    Log.d(TAG, throwableToString(e));
                    failure.add("");
                }
                subscriber.onNext(failure);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                Toast.makeText(fragment.getContext(), "cancel checkout failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<String> strings) {
                SysNaviagtionObservables.refresh(adapter, fragment);
                fragment.getActivity().invalidateOptionsMenu();
                if (strings.size() == 0)
                    Toast.makeText(fragment.getContext(), "cancel checkout succeeded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(fragment.getContext(), "cancel checkout failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String throwableToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement ee : e.getStackTrace())
            sb.append(ee.toString()).append("\n");
        return sb.toString();
    }

    private static void toastSuccess(BaseUIInterface baseUIInterface, String oper) {
        if (baseUIInterface instanceof Fragment)
            Toast.makeText(((Fragment) baseUIInterface).getContext(), oper + " done", Toast.LENGTH_LONG);
        else if (baseUIInterface instanceof AppCompatActivity)
            Toast.makeText(((AppCompatActivity) baseUIInterface), oper + " done", Toast.LENGTH_LONG);

    }

    private static void toastFailed(BaseUIInterface baseUIInterface, String oper) {
        if (baseUIInterface instanceof Fragment)
            Toast.makeText(((Fragment) baseUIInterface).getContext(), "some " + oper + " operations failed", Toast.LENGTH_LONG);
        else if (baseUIInterface instanceof AppCompatActivity)
            Toast.makeText(((AppCompatActivity) baseUIInterface), "some " + oper + " operations failed", Toast.LENGTH_LONG);

    }

    private static void toastOnError(BaseUIInterface baseUIInterface, String oper) {
        if (baseUIInterface instanceof Fragment)
            Toast.makeText(((Fragment) baseUIInterface).getContext(), "something went wrong when " + oper + "ing", Toast.LENGTH_LONG);
        else if (baseUIInterface instanceof AppCompatActivity)
            Toast.makeText(((AppCompatActivity) baseUIInterface), "something went wrong when " + oper + "ing", Toast.LENGTH_LONG);

    }


}
