package com.w3ma.androidautoalerts.mvp;

/**
 * Created by Emanuele on 05/06/2017.
 */

public interface MvpView {

    void showLoading();

    void showContent();

    void showMessage(int messageResId);

    void showMessage(String message);

    void finishActivity();

    void showMessage(int messageResId, String... parameters);
}
