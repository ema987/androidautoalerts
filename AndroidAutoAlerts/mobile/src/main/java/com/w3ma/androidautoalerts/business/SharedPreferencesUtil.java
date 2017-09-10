package com.w3ma.androidautoalerts.business;

import android.content.Context;
import android.content.SharedPreferences;

import com.w3ma.androidautoalerts.R;

/**
 * Created by ema987 on 8/27/2017.
 */
public class SharedPreferencesUtil {

    public static final String KEY_ASSISTANT_NAME = "key_assistant_name";
    private static final String KEY_SPEED_LIMIT = "key_speed_limit";
    private static final String KEY_ALERTS_ENABLED = "key_alerts_enabled";

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesUtil(final Context context, final SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public int getSpeedLimit() {
        return sharedPreferences.getInt(KEY_SPEED_LIMIT, context.getResources().getInteger(R.integer.pref_default_speed_limit));
    }

    public String getAssistantName() {
        return sharedPreferences.getString(KEY_ASSISTANT_NAME, context.getResources().getString(R.string.pref_default_assistant_name));
    }

    public boolean isAlertsEnabled() {
        return sharedPreferences.getBoolean(KEY_ALERTS_ENABLED, false);
    }

    public void setAlertsEnabled(final boolean alertsEnabled) {
        sharedPreferences.edit().putBoolean(KEY_ALERTS_ENABLED, alertsEnabled).apply();
    }
}
