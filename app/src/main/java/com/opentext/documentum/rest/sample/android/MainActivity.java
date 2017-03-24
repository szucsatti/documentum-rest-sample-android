/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.adapters.DrawerListAdapter;
import com.opentext.documentum.rest.sample.android.fragments.CabinetsFragment;
import com.opentext.documentum.rest.sample.android.fragments.GroupsFragment;
import com.opentext.documentum.rest.sample.android.fragments.UsersFragment;
import com.opentext.documentum.rest.sample.android.items.DrawerItem;
import com.opentext.documentum.rest.sample.android.util.AppCurrentUser;
import com.opentext.documentum.rest.sample.android.util.ThemeResolver;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final int WELCOME = 101;
    public static final int REQUEST_LOGIN = 102;
    private static final String TAG = "MainActivity";
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer_account)
    TextView drawerTextView;
    @BindView(R.id.drawer_list)
    ListView drawerListView;
    @BindView(R.id.drawer_toolbar)
    Toolbar drawerToolbar;

    //    @BindView(R.id.main_intro)
//    View introView;
    List<String>[] toolbarStrings;
    @BindArray(R.array.drawerlist_items)
    String[] navStrings;
    @BindView(R.id.log_out_button)
    Button logOut;
    int[] navDrawableIds;
    int[] navDrawableIdsSelected;
    DrawerItem[] drawerItems;
    Class[] fragmentClasses;
    Fragment[] fragments;
    Fragment cachedFragment;
    Fragment tmpFragment;
    ActionBarDrawerToggle drawerToggle;
    List<String> currentStringList;
    DrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer);
        ButterKnife.bind(this);
        initVariables();
        setUpToolbar();
        adapter = new DrawerListAdapter(this, R.layout.item_drawerlist, drawerItems);
        drawerListView.setAdapter(adapter);
        int[] colors = {0, ThemeResolver.resolve(getTheme(), R.attr.colorAccent), 0};
        drawerListView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        drawerListView.setDividerHeight(2);

        Intent intent = new Intent(MainActivity.this, CoverActivity.class);
        startActivityForResult(intent, WELCOME);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
//                introView.setVisibility(View.VISIBLE);
                resetToolbarStrings();
                resetCurrentStringList();
                resetTitle();
                adapter.setSelected(-1);
                drawerListView.invalidateViews();
                drawerLayout.closeDrawers();
                Intent intent = new Intent(MainActivity.this, CoverActivity.class);
                startActivityForResult(intent, WELCOME);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WELCOME && resultCode == -1) {
            drawerTextView.setText(" " + AppCurrentUser.getUsername());
            onDrawerItemClick(0);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initVariables() {
        Resources resources = getResources();
        // init Drawable items for adapter
        navDrawableIds = new int[navStrings.length];
        navDrawableIdsSelected = new int[navStrings.length];
        navDrawableIds[0] = R.drawable.vic_nav_cabinet_o;
        navDrawableIdsSelected[0] = R.drawable.vic_nav_cabinet;
        navDrawableIds[1] = R.drawable.vic_nav_user_o;
        navDrawableIdsSelected[1] = R.drawable.vic_nav_user;
        navDrawableIds[2] = R.drawable.vic_nav_group_o;
        navDrawableIdsSelected[2] = R.drawable.vic_nav_group;
//        navDrawableIds[3] = R.drawable.drawer_dql;
//        navDrawableIdsSelected[3] = R.drawable.drawer_dql_selected;
//        navDrawableIds[4] = R.drawable.drawer_settings;
//        navDrawableIdsSelected[4] = R.drawable.drawer_settings_selected;


        drawerItems = new DrawerItem[navStrings.length];
        for (int i = 0; i < drawerItems.length; ++i)
            drawerItems[i] = new DrawerItem(navStrings[i], navDrawableIdsSelected[i], navDrawableIds[i]);
        // fragments
        fragments = new Fragment[navStrings.length];
        fragmentClasses = new Class[navStrings.length];
        fragmentClasses[0] = CabinetsFragment.class;
        fragmentClasses[1] = UsersFragment.class;
        fragmentClasses[2] = GroupsFragment.class;
//        for (int i = 3; i < navStrings.length; ++i)
//            fragmentClasses[i] = CabinetsFragment.class;
        resetToolbarStrings();
    }

    private void resetCurrentStringList() {
        if (currentStringList != null)
            currentStringList.clear();
        else
            currentStringList = new LinkedList<String>();
        currentStringList.add(getResources().getString(R.string.app_name));
    }

    private void resetToolbarStrings() {
        toolbarStrings = new List[navStrings.length];
        for (int i = 0; i < navStrings.length; ++i) {
            toolbarStrings[i] = new LinkedList<>();
            toolbarStrings[i].add(navStrings[i]);
        }
    }

    void setUpToolbar() {
        setSupportActionBar(drawerToolbar);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);
        drawerTextView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.vic_currentuser), null, null, null);
    }

    void clearFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (tmpFragment != null) {
            ft.remove(tmpFragment);
            tmpFragment = null;
        }
        for (int i = 0; i < this.fragments.length; ++i)
            if (fragments[i] != null) {
                ft.remove(fragments[i]);
                fragments[i] = null;
            }
        cachedFragment = null;
        ft.commit();
    }

    ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, drawerToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @OnItemClick(R.id.drawer_list)
    void onDrawerItemClick(final int position) {
//        introView.setVisibility(View.GONE);
        adapter.setSelected(position);
        drawerListView.invalidateViews();
        //drawerListView.invalidate();

        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (fragments[position] == null)
                    try {
                        fragments[position] = (Fragment) fragmentClasses[position].newInstance();
                        ft.add(R.id.fragment_content, fragments[position]);
                    } catch (Exception e) {
                        Log.d(TAG, "new instance wrong");
                    }
                if (MainActivity.this.tmpFragment != null)
                    MainActivity.this.removeTmpFragment(tmpFragment);
                subscriber.onNext(ft);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {

            @Override
            public void call(Object o) {
                FragmentTransaction ft = (FragmentTransaction) o;
                for (Fragment fragment : fragments)
                    if (fragment != null && fragment != fragments[position])
                        ft.hide(fragment);
                ft.show(fragments[position]);
                MainActivity.this.cachedFragment = fragments[position];
                ft.commit();
                drawerLayout.closeDrawers();
                setCurrentTitleList(position);
            }
        });


    }

    public void attachTmpFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_content, fragment);
        ft.hide(cachedFragment);
        ft.show(fragment);
        ft.commit();
        this.tmpFragment = fragment;
    }

    public void removeTmpFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragment);
        ft.show(cachedFragment);
        ft.remove(fragment);
        ft.commit();
        this.tmpFragment = null;
    }

    public void setCurrentTitleList(int position) {
        this.currentStringList = toolbarStrings[position];
        resetTitle();
    }

    public void resetTitle() {
        if (currentStringList.size() > 1)
            drawerToolbar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        else drawerToolbar.findViewById(R.id.back_button).setVisibility(View.GONE);
        String tmp = "";
        for (String s : currentStringList)
            tmp += s + " /";
        ((TextView) drawerToolbar.findViewById(R.id.toolbar_title)).setTextSize(18);
        ((TextView) drawerToolbar.findViewById(R.id.toolbar_title)).setText(tmp.substring(0, tmp.length() - 1));
    }

    public void addStringAndResetToolbar(String s) {
        this.currentStringList.add(s);
        resetTitle();
    }

    public void removeTopStringAndResetToolbar() {
        if (this.currentStringList.size() == 1) {
            resetTitle();
            return;
        }
        this.currentStringList.remove(currentStringList.size() - 1);
        resetTitle();
    }
}
