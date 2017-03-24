/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    @BindView(R.id.loading_progress)
    View loadingView;
    @BindView(R.id.fragment_bk_text)
    TextView bkTextView;
    @BindView(R.id.fragment_main_component)
    View mainComponent;
    @BindView(R.id.filter_input)
    EditText filterInput;
    @BindView(R.id.filter_clear)
    ImageButton filterClearButton;
    @BindView(R.id.filter_layout)
    View filterLayout;
    @BindView(R.id.search_input)
    EditText searchInput;
    @BindView(R.id.search_clear)
    ImageButton searchClearButton;
    @BindView(R.id.search_layout)
    View searchLayout;

    private Unbinder unbinder;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupFilterLayout();
        setupSearchLayout();
        return view;
    }

    private void setupFilterLayout() {
        filterInput.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.vic_filter), null, null, null);
        filterClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setupSearchLayout() {
        searchInput.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.vic_search), null, null, null);
        searchClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        replaceMainComponent();
    }

    void replaceMainComponent() {
        ViewGroup parent = (ViewGroup) mainComponent.getParent();
        int index = parent.indexOfChild(mainComponent);
        parent.removeView(mainComponent);
        mainComponent = createMainComponent();
        parent.addView(mainComponent, index);
    }

    abstract View createMainComponent();

    public void resetBackground() {
        loadingView.setVisibility(View.GONE);
        bkTextView.setVisibility(View.GONE);
        mainComponent.setVisibility(View.GONE);
    }

    public void setMainComponentBackground() {
        resetBackground();
        mainComponent.setVisibility(View.VISIBLE);
    }

    public void setEmptyBackground() {
        resetBackground();
        bkTextView.setText(getString(R.string.empty_msg));
        bkTextView.setVisibility(View.VISIBLE);
    }

    public void setErrorBackground() {
        resetBackground();
        bkTextView.setText(getString(R.string.error_msg));
        bkTextView.setVisibility(View.VISIBLE);
    }

    public void setLoadingBackground() {
        resetBackground();
        loadingView.setVisibility(View.VISIBLE);
    }

    public void enableLoadingBackground() {
        loadingView.setVisibility(View.VISIBLE);
        mainComponent.setClickable(false);
        mainComponent.setAlpha(0.20f);
    }

    public void disableLoadingBackground() {
        loadingView.setVisibility(View.GONE);
        mainComponent.setClickable(true);
        mainComponent.setAlpha(1f);
    }


}
