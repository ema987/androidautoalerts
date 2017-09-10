package com.w3ma.androidautoalerts.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import com.w3ma.androidautoalerts.config.dagger.ApplicationModule;
import com.w3ma.androidautoalerts.config.dagger.DaggerApplicationComponent;
import com.w3ma.androidautoalerts.eventbus.NotificationReadEvent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * A receiver that gets called when a reply is sent to a given conversationId
 */
public class MessageReplyReceiver extends BroadcastReceiver {

    @Inject
    EventBus eventBus;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(context)).build().inject(this);
        if (MessagingService.REPLY_ACTION.equals(intent.getAction())) {
            final int conversationId = intent.getIntExtra(MessagingService.CONVERSATION_ID, -1);
            final CharSequence reply = getMessageText(intent);
            if (conversationId != -1) {
                Timber.d("Got reply (" + reply + ") for ConversationId " + conversationId);
                eventBus.post(new NotificationReadEvent());
            }
        }
    }

    /**
     * Get the message text from the intent.
     * Note that you should call {@code RemoteInput#getResultsFromIntent(intent)} to process
     * the RemoteInput.
     */
    private CharSequence getMessageText(final Intent intent) {
        final Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(MessagingService.EXTRA_VOICE_REPLY);
        }
        return null;
    }
}
