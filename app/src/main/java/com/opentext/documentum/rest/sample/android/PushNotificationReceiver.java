package com.opentext.documentum.rest.sample.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.opentext.documentum.rest.sample.android.fragments.CabinetsFragment;

public class PushNotificationReceiver extends BroadcastReceiver {

    private MainActivity mainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        CabinetsFragment fragment = getFragment();
        fragment.openFolder();
    }

    public void setMainActivity(final MainActivity main){
        this.mainActivity = main;
    }

    public CabinetsFragment getFragment(){
        return (CabinetsFragment)mainActivity.fragments[0];
    }
}
