/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.adapters.SysObjectListBaseAdapter;
import com.opentext.documentum.rest.sample.android.fragments.BaseUIInterface;
import com.opentext.documentum.rest.sample.android.observables.SetImageObservables;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


abstract public class MiniListBaseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, View.OnClickListener, BaseUIInterface {
    protected List<SysObjectListBaseAdapter> adapters = new LinkedList<>();
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.fragment_bk_image)
    ImageView bkImageView;
    @BindView(R.id.fragment_bk_text)
    TextView bkTextView;
    @BindView(R.id.fragment_main_component)
    View mainComponent;
    @BindView(R.id.two_buttons)
    View buttonsView;
    @BindView(R.id.remove_button)
    Button removeButton;
    List<String> currentStringList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_minichoice_base);
        ButterKnife.bind(this);
        setupToolbar();
        currentStringList = new LinkedList<>();
        replaceMainComponent();
    }


    abstract View createMainComponent();

    void replaceMainComponent() {
        ViewGroup parent = (ViewGroup) mainComponent.getParent();
        int index = parent.indexOfChild(mainComponent);
        parent.removeView(mainComponent);
        mainComponent = createMainComponent();
        if (mainComponent != null)
            parent.addView(mainComponent, index);
    }

    public void resetBackground() {
        bkImageView.setVisibility(View.GONE);
        bkTextView.setVisibility(View.GONE);
        mainComponent.setVisibility(View.GONE);
    }

    public void setMainComponentBackground() {
        resetBackground();
        mainComponent.setVisibility(View.VISIBLE);
    }

    public void setEmptyBackground() {
        resetBackground();
        bkTextView.setText(R.string.empty_msg);
        bkTextView.setVisibility(View.VISIBLE);
    }

    public void setErrorBackground() {
        resetBackground();
        bkTextView.setText(R.string.error_msg);
        bkTextView.setVisibility(View.VISIBLE);
    }

    public void setLoadingBackground() {
        resetBackground();
        bkImageView.setVisibility(View.VISIBLE);
        // TODO
        SetImageObservables.setImage(this, R.drawable.loading, bkImageView);
    }

    public void enableLoadingBackground() {
        bkImageView.setVisibility(View.VISIBLE);
        mainComponent.setClickable(false);
        mainComponent.setAlpha(0.20f);
    }

    public void disableLoadingBackground() {
        bkImageView.setVisibility(View.GONE);
        mainComponent.setClickable(true);
        mainComponent.setAlpha(1f);
    }


    //////////////////////////////
    // toolbar operations

    void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);

//        getSupportActionBar().setIcon(R.drawable.vic_pdf);
//        toolbar.setNavigationIcon(getDrawable(R.drawable.vic_pdf));

        toolbar.findViewById(R.id.back_button).setOnClickListener(this);
    }

    public void resetTitle() {
        if (currentStringList.size() > 1)
            toolbar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        else toolbar.findViewById(R.id.back_button).setVisibility(View.GONE);
        String tmp = "";
        for (String s : currentStringList)
            tmp += s + "/";
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setTextSize(18);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(tmp.substring(0, tmp.length() - 1));
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
    /////////////////////////////

    /////////////////////////////////////////////////////////////////////
    public void restoreLocation() {
        SysObjectListBaseAdapter lastAdapter = adapters.get(adapters.size() - 1);
        ((ListView) mainComponent).setSelectionFromTop(lastAdapter.getSelect(), lastAdapter.getScrollY());
    }

    void rememberLocation() {
        SysObjectListBaseAdapter lastAdapter = adapters.get(adapters.size() - 1);
        lastAdapter.setScrollY(getScrollY());
        lastAdapter.setSelect(getSelect());
    }

    int getScrollY() {
        View c = ((ListView) mainComponent).getChildAt(0);
        return c.getTop();
    }

    int getSelect() {
        return ((ListView) mainComponent).getFirstVisiblePosition();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE)
            rememberLocation();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    abstract public void onItemClick(AdapterView<?> parent, View view, int position, long id);


    @Override
    public void onClick(View v) {
        this.removeTopStringAndResetToolbar();
        if (adapters.size() == 1) {
            return;
        }
        adapters.remove(adapters.size() - 1);
        SysObjectListBaseAdapter lastAdapter = adapters.get(adapters.size() - 1);
        ((ListView) mainComponent).setAdapter(lastAdapter);
        if (lastAdapter.isEmpty()) setEmptyBackground();
        else setMainComponentBackground();
        restoreLocation();
        lastAdapter.notifyDataSetChanged();
    }

}
