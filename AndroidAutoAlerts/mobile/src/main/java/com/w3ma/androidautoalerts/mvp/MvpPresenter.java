package com.w3ma.androidautoalerts.mvp;

import android.os.Bundle;

/**
 * Created by Emanuele on 05/06/2017.
 */

public interface MvpPresenter {

    void onResume();

    void onCreate(Bundle extras);

    void onPause();
}
