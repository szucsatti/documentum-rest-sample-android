/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

public interface BaseUIInterface {
    public void resetBackground();

    public void setMainComponentBackground();

    public void setEmptyBackground();

    public void setErrorBackground();

    public void setLoadingBackground();

    public void enableLoadingBackground();

    public void disableLoadingBackground();

    public void restoreLocation();
}
