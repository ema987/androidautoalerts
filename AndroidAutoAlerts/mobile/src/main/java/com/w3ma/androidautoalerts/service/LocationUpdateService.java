package com.w3ma.androidautoalerts.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;
import com.w3ma.androidautoalerts.R;
import com.w3ma.androidautoalerts.auto.MessagingService;
import com.w3ma.androidautoalerts.business.SharedPreferencesUtil;
import com.w3ma.androidautoalerts.config.dagger.ApplicationModule;
import com.w3ma.androidautoalerts.config.dagger.DaggerApplicationComponent;
import com.w3ma.androidautoalerts.eventbus.NotificationReadEvent;
import com.w3ma.androidautoalerts.ui.main.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdateService extends Service {

    private static final String PACKAGE_NAME = "com.w3ma.androidautoalerts.service";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int FOREGROUND_SERVICE_NOTIFICATION_ID = 999;
    private static final double MPH_TO_KMH_CONVERSION_FACTOR = 3.6d;
    private static final String FOREGROUND_NOTIFICATION_CHANNEL = "foregroundNotificationChannel";
    private final IBinder mBinder = new LocalBinder();
    @Inject
    RxLocation rxLocation;
    @Inject
    SharedPreferencesUtil sharedPreferencesUtil;
    @Inject
    EventBus eventBus;
    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;
    private Observable<Location> locationObservable;
    // A reference to the service used to create notifications.
    private MessagingService messagingService = null;
    private boolean mBound;
    private final ServiceConnection messagingServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder service) {
            final MessagingService.LocalBinder binder = (MessagingService.LocalBinder) service;
            messagingService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            messagingService = null;
            mBound = false;
        }
    };
    private Disposable locationDisposable;
    private boolean speedLimitExceededNotified = false;

    @Override
    public void onCreate() {
        DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build().inject(this);

        eventBus.register(this);

        bindService(new Intent(this, MessagingService.class), messagingServiceConnection, Context.BIND_AUTO_CREATE);

        setAlertOptions();
    }

    @SuppressWarnings("all")
    private void setAlertOptions() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        //changes from settings are not immediately saved if press home startLocationServiceButton, service is still the same, that's why we must check sharedPreferences everytime
        locationObservable = rxLocation.location().updates(locationRequest).filter(location -> location.getSpeed() > sharedPreferencesUtil.getSpeedLimit() / MPH_TO_KMH_CONVERSION_FACTOR);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Timber.d("Service started");
        final boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    private void removeLocationUpdates() {
        locationDisposable.dispose();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        Timber.d("Service bind");
        // Called when a client comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        setAlertOptions();
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(final Intent intent) {
        Timber.d("Service rebind");
        // Called when a client returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        setAlertOptions();
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        Timber.d("Service unbind");
        // Called when the last client unbinds from this
        // service. If this method is called due to a configuration change we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && sharedPreferencesUtil.isAlertsEnabled()) {
            /*
            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesService.class), FOREGROUND_SERVICE_NOTIFICATION_ID, getNotification());
            } else {
                startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, getNotification());
            }
             */
            startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        Timber.d("Service onDestroy");
        if (mBound) {
            unbindService(messagingServiceConnection);
            mBound = false;
        }
        eventBus.unregister(this);
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        final Intent intent = new Intent(this, LocationUpdateService.class);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        final PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        final PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        return new NotificationCompat.Builder(this, FOREGROUND_NOTIFICATION_CHANNEL)
                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.stop_alerts),
                        servicePendingIntent)
                .setContentText(getString(R.string.foreground_service_notification_content))
                .setContentTitle(getString(R.string.foreground_service_notification_title))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.android_alert_white)
                .setWhen(System.currentTimeMillis()).build();
    }

    private void onNewLocation(final Location location) {
        Timber.d("New location: " + location);

        if (!speedLimitExceededNotified) {

            final String assistantName = sharedPreferencesUtil.getAssistantName();
            final String notificationString = getString(R.string.speed_limit_exceeded, sharedPreferencesUtil.getSpeedLimit());

            messagingService.sendNotification(assistantName, notificationString);

            speedLimitExceededNotified = true;
        }
    }

    @Subscribe
    public void onMessageEvent(final NotificationReadEvent event) {
        //sets notified to false so the notification will be shown again next time the event will be triggered
        speedLimitExceededNotified = false;
    }

    public void requestLocationUpdates() {
        Timber.d("Requesting location updates");
        sharedPreferencesUtil.setAlertsEnabled(true);
        startService(new Intent(getApplicationContext(), LocationUpdateService.class));
        if (locationDisposable != null) {
            locationDisposable.dispose();
        }
        locationDisposable = locationObservable.subscribe(this::onNewLocation);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public LocationUpdateService getService() {
            return LocationUpdateService.this;
        }

    }
}
