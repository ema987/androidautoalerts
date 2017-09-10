package com.w3ma.androidautoalerts.auto;

/**
 * Created by ema987 on 8/20/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.w3ma.androidautoalerts.config.dagger.ApplicationModule;
import com.w3ma.androidautoalerts.config.dagger.DaggerApplicationComponent;
import com.w3ma.androidautoalerts.eventbus.NotificationReadEvent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import timber.log.Timber;

public class MessageReadReceiver extends BroadcastReceiver {

    @Inject
    EventBus eventBus;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(context)).build().inject(this);
        if (MessagingService.READ_ACTION.equals(intent.getAction())) {
            final int conversationId = intent.getIntExtra(MessagingService.CONVERSATION_ID, -1);
            if (conversationId != -1) {
                Timber.d("Conversation " + conversationId + " was read");
                NotificationManagerCompat.from(context).cancel(conversationId);
                eventBus.post(new NotificationReadEvent());
            }
        }
    }
}
