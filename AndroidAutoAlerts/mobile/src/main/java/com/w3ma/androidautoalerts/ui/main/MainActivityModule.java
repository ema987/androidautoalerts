package com.w3ma.androidautoalerts.ui.main;

import com.w3ma.androidautoalerts.config.dagger.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ema987 on 8/22/2017.
 */
@Module
public class MainActivityModule {

    private final MainActivityContract.View view;

    public MainActivityModule(final MainActivityContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MainActivityContract.View provideView() {
        return view;
    }

    @ActivityScope
    @Provides
    MainActivityContract.Presenter providePresenter() {
        return new MainActivityPresenter(view);
    }

}
