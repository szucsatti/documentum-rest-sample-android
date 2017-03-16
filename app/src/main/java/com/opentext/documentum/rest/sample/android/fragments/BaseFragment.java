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
import android.widget.ImageView;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.observables.SetImageObservables;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    @BindView(R.id.fragment_bk_image)
    ImageView bkImageView;
    @BindView(R.id.fragment_bk_text)
    TextView bkTextView;
    @BindView(R.id.fragment_main_component)
    View mainComponent;

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
        return view;
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
        bkTextView.setText("EMPTY HERE :)");
        bkTextView.setVisibility(View.VISIBLE);
    }

    public void setErrorBackground() {
        resetBackground();
        bkTextView.setText("ERROR :(");
        bkTextView.setVisibility(View.VISIBLE);
    }

    public void setLoadingBackground() {
        resetBackground();
        bkImageView.setVisibility(View.VISIBLE);
        // TODO
        SetImageObservables.setImage(getContext(), R.drawable.loading, bkImageView);
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


}
