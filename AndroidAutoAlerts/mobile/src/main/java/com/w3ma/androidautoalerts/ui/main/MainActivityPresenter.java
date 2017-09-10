package com.w3ma.androidautoalerts.ui.main;

import android.os.Bundle;

/**
 * Created by ema987 on 8/22/2017.
 */
public class MainActivityPresenter implements MainActivityContract.Presenter {

    private final MainActivityContract.View view;

    public MainActivityPresenter(final MainActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onCreate(final Bundle extras) {

    }

    @Override
    public void onPause() {

    }

}
