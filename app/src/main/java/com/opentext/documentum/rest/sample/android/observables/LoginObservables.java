/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.observables;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestErrorException;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.Repository;
import com.emc.documentum.rest.client.sample.model.RestError;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.opentext.documentum.rest.sample.android.LoginActivity;
import com.opentext.documentum.rest.sample.android.util.AccountHelper;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;
import com.opentext.documentum.rest.sample.android.util.AppDCTMClientBuilder;
import com.opentext.documentum.rest.sample.android.util.TypeInfoHelper;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LoginObservables {
    private static final String TAG = "LoginObservables";
    private static final int TIME_OUT = 20000;


    public static void getProductInfoAndRepos(final LoginActivity loginActivity) {
        AppDCTMClientBuilder.contextRoot(loginActivity.getContextRoot());
        loginActivity.setContextRootEnable(false);
        loginActivity.setRepoViewEnable(false);

        // get product info
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                RestObject info = client.getProductInfo();
                StringBuilder sb = new StringBuilder();
                for (String key : info.getProperties().keySet()) {
                    sb.append(key).append(":").append(info.getProperties().get(key)).append("\n");
                    if (key.equals("major"))
                        AccountHelper.setProductMajor(loginActivity, Float.valueOf(info.getProperties().get(key).toString()));
                }
                subscriber.onNext(sb.toString());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).timeout(TIME_OUT, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                loginActivity.setContextRootEnable(true);
                loginActivity.setConsoleInfo(throwableToString(e), true);
            }

            @Override
            public void onNext(String s) {
                loginActivity.setContextRootEnable(true);
                loginActivity.setConsoleInfo(s, false);
                loginActivity.setEnableLogin();
            }
        });

        // get repository list
        Observable.create(new Observable.OnSubscribe<String[]>() {
            @Override
            public void call(Subscriber<? super String[]> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                Feed<Repository> feed = client.getRepositories();
                String[] repos = feed.getEntries() == null ? new String[0] : new String[feed.getEntries().size()];
                for (int i = 0; i < repos.length; ++i)
                    repos[i] = feed.getEntries().get(i).getTitle();
                subscriber.onNext(repos);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).timeout(TIME_OUT, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String[]>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                loginActivity.setRepoViewEnable(false);
                Toast.makeText(loginActivity, "Can't get repositories", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String[] strings) {
                loginActivity.setRepoViewEnable(true);
                loginActivity.resetRepos(strings);
            }
        });
    }

    public static void login(final LoginActivity loginActivity) {
        loginActivity.setLoadingLayout();
        AccountHelper.setAccountAndBuilder(loginActivity, loginActivity.getRemeber(), loginActivity.getContextRoot(),
                loginActivity.getId(), loginActivity.getPassword(), loginActivity.getRepo());
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                DCTMRestClient client = AppDCTMClientBuilder.build();
                client.getRepository();
                // save current user info
                AppCurrentUser.set(client.getCurrentUser());
                // init type meta
                if (!TypeInfoHelper.INSTANCE.hasType("dm_user")) {
                    TypeInfoHelper.INSTANCE.setupTypeInfo(client.getType("dm_user"));
                }
                if (!TypeInfoHelper.INSTANCE.hasType("dm_group")) {
                    TypeInfoHelper.INSTANCE.setupTypeInfo(client.getType("dm_group"));
                }
                if (!TypeInfoHelper.INSTANCE.hasType("dm_sysobject")) {
                    TypeInfoHelper.INSTANCE.setupTypeInfo(client.getType("dm_sysobject"));
                }
                subscriber.onNext(null);
                subscriber.onCompleted();

            }
        }).timeout(TIME_OUT, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                loginActivity.setResult(Activity.RESULT_OK);
                loginActivity.finish();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, throwableToString(e));
                Toast.makeText(loginActivity, "something went wrong when logging in", Toast.LENGTH_LONG);
                loginActivity.setLoginMainContent();
                if (e instanceof DCTMRestErrorException) {
                    DCTMRestErrorException restError = (DCTMRestErrorException) e;
                    loginActivity.setConsoleInfo(errorToString(restError.getError()), true);
                }
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    private static String errorToString(RestError error) {
        StringBuilder sb = new StringBuilder();
        sb.append("Status --> ").append(error.getStatus()).append("\r\n")
                .append("Code --> ").append(error.getCode()).append("\r\n")
                .append("Message --> ").append(error.getMessage()).append("\r\n")
                .append("Cause --> ").append(error.getDetails());
        return sb.toString();
    }


    private static String throwableToString(Throwable e) {
        return e.getClass().getCanonicalName() + " --> " + e.getMessage();
    }
}
