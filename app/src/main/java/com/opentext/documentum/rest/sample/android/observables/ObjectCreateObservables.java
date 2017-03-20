/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.observables;

import android.util.Log;
import android.widget.Toast;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;
import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.fragments.BaseUIInterface;
import com.opentext.documentum.rest.sample.android.fragments.ObjectCreateFragment;
import com.opentext.documentum.rest.sample.android.util.AppDCTMClientBuilder;

import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ObjectCreateObservables {
    public static final String TAG = "ObjectCreateObservables";

    public static void create(final ObjectCreateFragment objectCreateFragment,
                              final SysObjectListBaseAdapter sourceAdapter,
                              final BaseUIInterface sourceUiInterface) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                String[] args = getArgs(objectCreateFragment.getProperties());
                switch (objectCreateFragment.getMenuItemId()) {
                    case R.id.create_folder:
                        RestObject folder = new PlainRestObject(args);
                        try {
                            client.createFolder(client.getObject(objectCreateFragment.getObjectId()), folder);
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                        break;
                    case R.id.create_document:
                        client.createDocument(client.getObject(objectCreateFragment.getObjectId()), new PlainRestObject(args), objectCreateFragment.getContentBytes(), null);
                        break;
                    case R.id.create_user:
                        client.createUser(new PlainRestObject(args));
                        break;
                    case R.id.create_cabinet:
                        RestObject cabinet = new PlainRestObject(args);
                        client.createCabinet(cabinet);
                        break;
                    case R.id.create_group:
                        if (objectCreateFragment.getObjectId() == null)
                            client.createGroup(new PlainRestObject(args));
                        else {
                            RestObject newGroup = client.createGroup(new PlainRestObject(args));
                            client.addGroupToGroup(client.getGroup(objectCreateFragment.getObjectId()), newGroup);
                        }
                        break;
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(objectCreateFragment.getContext(), "create failed", Toast.LENGTH_LONG).show();
                Log.d(TAG, throwableToString(e));
            }

            @Override
            public void onNext(Object o) {
                ((MainActivity) objectCreateFragment.getActivity()).removeTmpFragment(objectCreateFragment);
                SysNaviagtionObservables.refresh(sourceAdapter, sourceUiInterface);
                Toast.makeText(objectCreateFragment.getContext(), "create succeeded", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void checkIn(final ObjectCreateFragment objectCreateFragment,
                               final SysObjectListBaseAdapter sourceAdapter,
                               final BaseUIInterface sourceUiInterface) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                String[] args = getArgs(objectCreateFragment.getEditableProperties());
                switch (objectCreateFragment.getMenuItemId()) {
                    case R.id.check_in_major:
                        client.checkinNextMajor(client.getObject(objectCreateFragment.getObjectId()), new PlainRestObject(args), objectCreateFragment.getContentBytes(), null);
                        break;
                    case R.id.check_in_minor:
                        client.checkinNextMinor(client.getObject(objectCreateFragment.getObjectId()), new PlainRestObject(args), objectCreateFragment.getContentBytes(), null);
                        break;
                    case R.id.check_in_branch:
                        client.checkinBranch(client.getObject(objectCreateFragment.getObjectId()), new PlainRestObject(args), objectCreateFragment.getContentBytes(), null);
                        break;
                }
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                //todo: refresh here
                SysNaviagtionObservables.refresh(sourceAdapter, sourceUiInterface);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(objectCreateFragment.getContext(), "check in failed", Toast.LENGTH_LONG).show();
                Log.d(TAG, throwableToString(e));
            }

            @Override
            public void onNext(Object o) {
                SysNaviagtionObservables.refresh(sourceAdapter, sourceUiInterface);
                Toast.makeText(objectCreateFragment.getContext(), "checkin succeeded", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String[] getArgs(Map<String, String> proper) {
        String[] args = new String[proper.size() * 2];
        int idx = 0;
        for (String k : proper.keySet()) {
            args[idx++] = k;
            args[idx++] = proper.get(k);
        }
        return args;
    }

    private static String throwableToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement ee : e.getStackTrace())
            sb.append(ee.toString()).append("\n");
        return sb.toString();
    }


}
