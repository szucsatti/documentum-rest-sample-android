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
import android.widget.PopupWindow;
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
    @BindView(R.id.filter_up)
    ImageButton filterUpButton;
    @BindView(R.id.filter_down)
    ImageButton filterDownButton;
    @BindView(R.id.filter_down_o)
    ImageButton filterDownOutlineButton;
    @BindView(R.id.filter_confirm)
    ImageButton filterConfirmButton;
    @BindView(R.id.filter_layout)
    View filterLayout;

    @BindView(R.id.search_input)
    EditText searchInput;
    @BindView(R.id.search_clear)
    ImageButton searchClearButton;
    @BindView(R.id.search_confirm)
    ImageButton searchConfirmButton;
    @BindView(R.id.search_layout)
    View searchLayout;

    PopupWindow popupFilter;

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
        setupPopupFilter();
        return view;
    }

    private void setupPopupFilter() {
        popupFilter = new PopupWindow(getContext());
        View popupFilterView = LayoutInflater.from(getContext()).inflate(R.layout.layout_filter, null);
        popupFilter.setContentView(popupFilterView);
        popupFilter.setAnimationStyle(R.style.PopupAnm);
        popupFilter.setElevation(20);
    }

    private void setupFilterLayout() {
        filterInput.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.vic_filter), null, null, null);
        filterUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterUpButton.setVisibility(View.GONE);
                //todo if else
                filterDownOutlineButton.setVisibility(View.GONE);
                filterDownButton.setVisibility(View.VISIBLE);
                filterInput.setEnabled(true);
                popupFilter.dismiss();
            }
        });
        filterDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDownOutlineButton.setVisibility(View.GONE);
                filterDownButton.setVisibility(View.GONE);
                filterUpButton.setVisibility(View.VISIBLE);
                filterInput.setEnabled(false);
                popupFilter.showAsDropDown(filterLayout);
            }
        });
        filterDownOutlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDownButton.setVisibility(View.GONE);
                filterDownOutlineButton.setVisibility(View.GONE);
                filterUpButton.setVisibility(View.VISIBLE);
                filterInput.setEnabled(false);
                popupFilter.showAsDropDown(filterLayout);
            }
        });
        filterConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshOnFilter();
            }
        });
        filterClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterInput.setText(null);
                filterLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setupSearchLayout() {
        searchInput.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.vic_search), null, null, null);
        searchConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshOnSearch();
            }
        });
        searchClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText(null);
                searchLayout.setVisibility(View.GONE);
            }
        });
    }

    abstract void refreshOnFilter();

    abstract void refreshOnSearch();

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
