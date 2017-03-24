/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.util;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.opentext.documentum.rest.sample.android.MiniListBaseActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.fragments.SysObjectNavigationBaseFragment;

public enum ViewFactory {
    INSTANCE;

    public ListView newListView(Context context, SysObjectNavigationBaseFragment fragment) {
        return newListView(context, fragment, fragment);
    }

    public ListView newListView(MiniListBaseActivity activity) {
        return newListView(activity, activity, activity);
    }

    private ListView newListView(Context context, AdapterView.OnItemClickListener clickListener, AbsListView.OnScrollListener scrollListener) {
        ListView listView = new ListView(context);
        listView.setOnItemClickListener(clickListener);
        listView.setOnScrollListener(scrollListener);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(ThemeResolver.resolve(context.getTheme(), R.attr.colorPrimary));
        int[] colors = {0, context.getColor(R.color.appAccent), 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(2);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        return listView;
    }
}
