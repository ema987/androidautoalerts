package com.w3ma.androidautoalerts.ui.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tedpark.tedpermission.rx2.TedRx2Permission;
import com.w3ma.androidautoalerts.R;
import com.w3ma.androidautoalerts.config.AndroidAutoAlertsApplication;
import com.w3ma.androidautoalerts.mvp.AbstractMvpActivity;
import com.w3ma.androidautoalerts.service.LocationUpdateService;
import com.w3ma.androidautoalerts.ui.about.AboutActivity;
import com.w3ma.androidautoalerts.ui.settings.SettingsActivity;

import butterknife.BindView;

public class MainActivity extends AbstractMvpActivity<MainActivityContract.Presenter> implements MainActivityContract.View {

    @BindView(R.id.startLocationServiceButton)
    Button button;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    // A reference to the service used to get location updates.
    private LocationUpdateService locationUpdateService = null;
    private boolean locationUpdateServiceBound;
    // Monitors the state of the connection to the service.
    private final ServiceConnection locationUpdateServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            final LocationUpdateService.LocalBinder binder = (LocationUpdateService.LocalBinder) service;
            locationUpdateService = binder.getService();
            locationUpdateServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            locationUpdateService = null;
            locationUpdateServiceBound = false;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TedRx2Permission.with(MainActivity.this)
                        .setGotoSettingButton(true)
                        .setRationaleTitle("PERMISSION")
                        .setRationaleMessage("we need permission for read contact and find your location") //
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                        .request()
                        .subscribe(tedPermissionResult -> {
                            if (tedPermissionResult.isGranted()) {
                                locationUpdateService.requestLocationUpdates();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Permission Denied\n" + tedPermissionResult.getDeniedPermissions().toString(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }, throwable -> {
                        }, () -> {
                        });
            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    protected void inject() {
        DaggerMainActivityComponent.builder().applicationComponent(((AndroidAutoAlertsApplication) getApplication()).getApplicationComponent())
                .mainActivityModule(new MainActivityModule(this)).build().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationUpdateService.class), locationUpdateServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationUpdateServiceBound) {
            unbindService(locationUpdateServiceConnection);
            locationUpdateServiceBound = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final Intent intentToLaunch;
        switch (item.getItemId()) {
            case R.id.actionSettings:
                intentToLaunch = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.actionAbout:
                intentToLaunch = new Intent(MainActivity.this, AboutActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        startActivity(intentToLaunch);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }
}
