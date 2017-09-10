package com.w3ma.androidautoalerts.ui.main;

import com.w3ma.androidautoalerts.config.dagger.ActivityScope;
import com.w3ma.androidautoalerts.config.dagger.ApplicationComponent;

import dagger.Component;

/**
 * Created by ema987 on 8/22/2017.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = MainActivityModule.class)
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);
}
