package com.w3ma.androidautoalerts.config.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;

import com.patloew.rxlocation.RxLocation;
import com.w3ma.androidautoalerts.business.Constants;
import com.w3ma.androidautoalerts.business.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Emanuele on 06/06/2017.
 */
@Module
public class ApplicationModule {

    private final Context context;

    public ApplicationModule(final Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences() {
        return context.getSharedPreferences(Constants.NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    public RxLocation provideRxLocation() {
        return new RxLocation(context);
    }

    @Singleton
    @Provides
    public NotificationManagerCompat provideNotificationManagerCompat(final Context context) {
        return NotificationManagerCompat.from(context);
    }

    @Singleton
    @Provides
    public SharedPreferencesUtil provideSharedPreferencesUtil(final Context context, final SharedPreferences sharedPreferences) {
        return new SharedPreferencesUtil(context, sharedPreferences);
    }

    @Singleton
    @Provides
    public EventBus provideEventBus() {
        return EventBus.getDefault();
    }

}
