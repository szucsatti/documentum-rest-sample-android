/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.observables;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.Link;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;
import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.enums.DctmPropertyName;
import com.opentext.documentum.rest.sample.android.fragments.BaseUIInterface;
import com.opentext.documentum.rest.sample.android.fragments.ObjectBaseFragment;
import com.opentext.documentum.rest.sample.android.fragments.ObjectDetailFragment;
import com.opentext.documentum.rest.sample.android.items.ObjectDetailItem;
import com.opentext.documentum.rest.sample.android.util.AppDCTMClientBuilder;
import com.opentext.documentum.rest.sample.android.util.FileUtil;
import com.opentext.documentum.rest.sample.android.util.MimeIconHelper;
import com.opentext.documentum.rest.sample.android.util.RestObjectUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.emc.documentum.rest.client.sample.model.LinkRelation.ENCLOSURE;


public class ObjectDetailObservables {
    private static String TAG = "ObjectDetailObservables";

    // used for update object
    private static ObjectDetailItem[] originItems;

    public static void loadObjectContent(final ObjectDetailFragment fragment) {
        final String KEY_TEXT = "TEXT";
        final String KEY_IMAGE = "IMAGE";
        final String KEY_OTHER = "OTHER";
        Observable.create(new Observable.OnSubscribe<Map<String, byte[]>>() {
            @Override
            public void call(Subscriber<? super Map<String, byte[]>> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                RestObject document = client.getDocument(fragment.getObjectId());
                Feed<RestObject> contentFeed = client.getContents(document, "media-url-policy", "local", "inline", "true");
                if (contentFeed.getEntries() == null) {
                    subscriber.onNext(null);
                } else {
                    //todo: only check local?
                    Map<String, byte[]> object = new HashMap<String, byte[]>();
                    OUTER_LOOP:
                    for (Entry<RestObject> entry : contentFeed.getEntries())
                        for (Link link : entry.getLinks())
                            if (link.getTitle() != null && link.getTitle().equals("LOCAL")) {
                                String fileName = document.getProperties().get("object_name").toString();
                                if (MimeIconHelper.isTxt(fileName)) {
                                    object.put(KEY_TEXT, client.getContentBytes(link.getHref()));
                                } else if (MimeIconHelper.isImage(fileName)) {
                                    object.put(KEY_IMAGE, client.getContentBytes(link.getHref()));
                                } else
                                    object.put(KEY_OTHER, client.getContentBytes(link.getHref()));
                                break OUTER_LOOP;
                            }
                    subscriber.onNext(object);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Map<String, byte[]>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                fragment.setUnsupportedContent(null);
            }

            @Override
            public void onNext(Map<String, byte[]> o) {
                if (o == null) {
                    fragment.setUnsupportedContent(null);
                    return;
                }
                if (o.containsKey(KEY_TEXT))
                    fragment.setViewTextContent(o.get(KEY_TEXT));
                else if (o.containsKey(KEY_IMAGE))
                    fragment.setImageContent(o.get(KEY_IMAGE));
                else {
                    fragment.setContentBytes(o.get(KEY_OTHER));
                    fragment.setUnsupportedContent(null);
                }
            }
        });
    }

    public static void loadObjectProperties(final ObjectDetailFragment fragment) {
        Observable.create(new Observable.OnSubscribe<ObjectDetailItem[]>() {
            @Override
            public void call(Subscriber<? super ObjectDetailItem[]> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                try {
                    RestObject object = fragment.getRestObject() == null ? client.getObject(fragment.getObjectId()) : fragment.getRestObject();
                    String type = object.getName();
                    ObjectDetailItem[] items = new ObjectDetailItem[object.getProperties().size()];
                    int idx = 0;
                    for (String k : object.getProperties().keySet()) {
                        String v = object.getProperties().get(k) == null ? "" : object.getProperties().get(k).toString();
                        items[idx++] = new ObjectDetailItem(type, k, v);
                    }
                    subscriber.onNext(items);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ObjectDetailItem[]>() {
            @Override
            public void call(ObjectDetailItem[] items) {
                originItems = items;
                fragment.updateAdapterItems(items, false);
            }
        });
    }

    public static void updateObject(final ObjectDetailFragment fragment,
                                    final SysObjectListBaseAdapter sourceAdapter,
                                    final BaseUIInterface sourceUiInterface) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                Map<String, String> propers = fragment.getEditableProperties();
                String[] args;
                int length = propers.size();
                if (propers.containsKey(DctmPropertyName.USER_NAME))
                    --length;
                if (propers.containsKey(DctmPropertyName.GROUP_NAME))
                    --length;
                args = new String[length * 2];

                int idx = 0;
                for (String k : propers.keySet()) {
                    if (k.equals(DctmPropertyName.USER_NAME) || k.equals(DctmPropertyName.GROUP_NAME))
                        continue;
                    args[idx++] = k;
                    args[idx++] = propers.get(k);
                }
                RestObject updated = new PlainRestObject(args);
                DCTMRestClient client = AppDCTMClientBuilder.build();
                client.update(client.getObject(fragment.getObjectId()), updated);
                subscriber.onNext(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                Toast.makeText(fragment.getContext(), "update failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(Object o) {
                ((MainActivity) fragment.getActivity()).removeTmpFragment(fragment);
                //todo: refresh
                SysNaviagtionObservables.refresh(sourceAdapter, sourceUiInterface);
                Toast.makeText(fragment.getContext(), "update succeeded", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void downloadObject(final RestObject doc, final Fragment fragment) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                File file = FileUtil.newFile(RestObjectUtil.getString(doc, DctmPropertyName.OBJECT_NAME), fragment);
                if (file == null) {
                    subscriber.onError(new Exception("Cant create directory"));
                }
                try {
                    DCTMRestClient client = AppDCTMClientBuilder.build();
                    RestObject primaryContent = client.getPrimaryContent(doc, "media-url-policy", "local");
                    byte[] data = client.getContentBytes(primaryContent.getHref(ENCLOSURE));
                    FileOutputStream fs = new FileOutputStream(file);
                    fs.write(data);
                    fs.close();
                    subscriber.onNext(file.getAbsolutePath());
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                Toast.makeText(fragment.getContext(), "can't save this file", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Object o) {
                Toast.makeText(fragment.getContext(), "file has been saved to " + o.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void downloadObject(final ObjectBaseFragment fragment) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                File file = FileUtil.newFile(fragment.getObjectName(), fragment);
                if (file == null) {
                    subscriber.onError(new Exception("Cant create directory"));
                }
                try {
                    FileOutputStream fs = new FileOutputStream(file);
                    fs.write(fragment.getContentBytes());
                    fs.close();
                    subscriber.onNext(file.getAbsolutePath());
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                Toast.makeText(fragment.getContext(), "can't save this file", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Object o) {
                Toast.makeText(fragment.getContext(), "file has been saved to " + o.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void checkIn(final ObjectDetailFragment fragment,
                               final SysObjectListBaseAdapter sourceAdapter,
                               final BaseUIInterface sourceUiInterface) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                String[] args = getArgs(fragment.getEditableProperties());
                switch (fragment.getMenuItemId()) {
                    case R.id.check_in_major:
                        client.checkinNextMajor(client.getObject(fragment.getObjectId()), new PlainRestObject(args), fragment.getContentBytes(), null);
                        break;
                    case R.id.check_in_minor:
                        client.checkinNextMinor(client.getObject(fragment.getObjectId()), new PlainRestObject(args), fragment.getContentBytes(), null);
                        break;
                    case R.id.check_in_branch:
                        client.checkinBranch(client.getObject(fragment.getObjectId()), new PlainRestObject(args), fragment.getContentBytes(), null);
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
                Toast.makeText(fragment.getContext(), "check in failed", Toast.LENGTH_LONG).show();
                Log.d(TAG, throwableToString(e));
            }

            @Override
            public void onNext(Object o) {
                ((MainActivity) fragment.getActivity()).removeTmpFragment(fragment);
                SysNaviagtionObservables.refresh(sourceAdapter, sourceUiInterface);
                Toast.makeText(fragment.getContext(), "checkin succeeded", Toast.LENGTH_LONG).show();
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

    public static String[] getArgs(Map<String, String> proper) {
        String[] args = new String[proper.size() * 2];
        int idx = 0;
        for (String k : proper.keySet()) {
            args[idx++] = k;
            args[idx++] = proper.get(k);
        }
        return args;
    }

}
