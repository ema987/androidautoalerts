package com.w3ma.androidautoalerts.config;

import android.app.Application;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.leakcanary.LeakCanary;
import com.w3ma.androidautoalerts.BuildConfig;
import com.w3ma.androidautoalerts.config.dagger.ApplicationComponent;
import com.w3ma.androidautoalerts.config.dagger.ApplicationModule;
import com.w3ma.androidautoalerts.config.dagger.DaggerApplicationComponent;

import timber.log.Timber;

/**
 * Created by Emanuele on 06/06/2017.
 */

public class AndroidAutoAlertsApplication extends Application {

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(final int priority, final String tag, final String message, final Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FirebaseCrash.logcat(priority, tag, message);

            if (t != null) {
                FirebaseCrash.report(t);
            }
        }
    }
}
