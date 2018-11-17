package com.opentext.documentum.rest.sample.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.opentext.documentum.rest.sample.android.fragments.BaseFragment;
import com.opentext.documentum.rest.sample.android.fragments.CabinetsFragment;

public class PushNotificationReceiver extends BroadcastReceiver {

    private MainActivity mainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        CabinetsFragment fragment = (CabinetsFragment)mainActivity.fragments[0];

        Object parent = mainActivity.findViewById(R.id.drawer_list);

        Object test = mainActivity.findViewById(R.id.drawer_layout);

        fragment.onItemClick((AdapterView<?>) parent, (View) test, 1, 0);

    }

    public void setMainActivity(final MainActivity main){
        this.mainActivity = main;
    }
}
