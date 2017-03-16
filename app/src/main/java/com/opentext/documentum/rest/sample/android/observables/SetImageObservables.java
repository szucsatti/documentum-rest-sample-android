/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.observables;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class SetImageObservables {
    public static void setImage(final Context context, final int resouceId, final ImageView imageView) {
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = context.getDrawable(resouceId);
                subscriber.onNext(drawable);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Drawable>() {
            @Override
            public void call(Drawable drawable) {
                imageView.setImageDrawable(drawable);
            }
        });
    }
}
