package com.w3ma.androidautoalerts.auto;

/**
 * Created by ema987 on 8/20/2017.
 */

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.CarExtender;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;

import com.w3ma.androidautoalerts.R;
import com.w3ma.androidautoalerts.config.dagger.ApplicationModule;
import com.w3ma.androidautoalerts.config.dagger.DaggerApplicationComponent;

import javax.inject.Inject;

public class MessagingService extends Service {
    public static final String READ_ACTION = "com.w3ma.androidautoalerts.ACTION_MESSAGE_READ";
    public static final String REPLY_ACTION = "com.w3ma.androidautoalerts.ACTION_MESSAGE_REPLY";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    public static final String EXTRA_REMOTE_REPLY = "extra_remote_reply";
    private static final String ALERT_NOTIFICATION_CHANNEL = "alertNotificationChannel";
    private static final int ALERT_NOTIFICATION_ID = 1;
    private final IBinder messagingServiceBinder = new MessagingService.LocalBinder();
    @Inject
    NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onCreate() {
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build().inject(this);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return messagingServiceBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    // Creates an Intent that will be triggered when a voice reply is received.
    private Intent getMessageReplyIntent(final int conversationId) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(REPLY_ACTION)
                .putExtra(CONVERSATION_ID, conversationId);
    }

    // Creates an intent that will be triggered when a message is marked as read.
    private Intent getMessageReadIntent(final int id) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(READ_ACTION)
                .putExtra(CONVERSATION_ID, id);
    }

    private void sendNotification(final int conversationId, final String message,
                                  final String participant, final long timestamp) {
        // A pending Intent for reads
        final PendingIntent readPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                conversationId,
                getMessageReadIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build a RemoteInput for receiving voice input in a Car Notification or text input on
        // devices that support text input (like devices on Android N and above).
        final RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_REMOTE_REPLY)
                .setLabel(getResources().getString(R.string.reply))
                .build();

        // Building a Pending Intent for the reply action to trigger
        final PendingIntent replyIntent = PendingIntent.getBroadcast(getApplicationContext(),
                conversationId,
                getMessageReplyIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build an Android N compatible Remote Input enabled action.
        final NotificationCompat.Action actionReplyByRemoteInput = new NotificationCompat.Action.Builder(
                R.drawable.android_alert_white, getResources().getString(R.string.reply), replyIntent)
                .addRemoteInput(remoteInput)
                .build();

        // Create the UnreadConversation and populate it with the participant name,
        // read and reply intents.
        final UnreadConversation.Builder unreadConvBuilder =
                new UnreadConversation.Builder(participant)
                        .addMessage(message)
                        .setLatestTimestamp(timestamp)
                        .setReadPendingIntent(readPendingIntent)
                        .setReplyAction(replyIntent, remoteInput);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ALERT_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_info_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getApplicationContext().getResources(), R.drawable.android_alert))
                .setContentText(message)
                .setWhen(timestamp)
                .setContentTitle(participant)
                .setAutoCancel(true)
                .setContentIntent(readPendingIntent)
                .extend(new CarExtender()
                        .setUnreadConversation(unreadConvBuilder.build())
                        .setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white)))
                .addAction(actionReplyByRemoteInput);


        notificationManagerCompat.notify(conversationId, builder.build());
    }

    public void sendNotification(final String participantName, final String notificationString) {
        sendNotification(ALERT_NOTIFICATION_ID, notificationString, participantName, System.currentTimeMillis());
    }

    public class LocalBinder extends Binder {

        public MessagingService getService() {
            return MessagingService.this;
        }

    }
}
