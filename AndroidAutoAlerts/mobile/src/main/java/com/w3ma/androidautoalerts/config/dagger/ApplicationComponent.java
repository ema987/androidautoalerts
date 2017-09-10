package com.w3ma.androidautoalerts.config.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;

import com.patloew.rxlocation.RxLocation;
import com.w3ma.androidautoalerts.auto.MessageReadReceiver;
import com.w3ma.androidautoalerts.auto.MessageReplyReceiver;
import com.w3ma.androidautoalerts.auto.MessagingService;
import com.w3ma.androidautoalerts.business.SharedPreferencesUtil;
import com.w3ma.androidautoalerts.config.AndroidAutoAlertsApplication;
import com.w3ma.androidautoalerts.service.LocationUpdateService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Emanuele on 06/06/2017.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    //interface methods for each @Provides method in the modules
    Context provideContext();

    SharedPreferences provideSharedPreferences();

    RxLocation provideRxLocation();

    NotificationManagerCompat provideNotificationManagerCompat();

    SharedPreferencesUtil provideSharedPreferencesUtil();

    EventBus provideEventBus();

    void inject(LocationUpdateService locationUpdateService);

    void inject(MessagingService messagingService);

    void inject(MessageReadReceiver messageReadReceiver);

    void inject(MessageReplyReceiver messageReplyReceiver);


    class Injector {
        public static ApplicationComponent getComponent(final Context c) {
            return ((AndroidAutoAlertsApplication) c.getApplicationContext()).getApplicationComponent();
        }
    }

}
