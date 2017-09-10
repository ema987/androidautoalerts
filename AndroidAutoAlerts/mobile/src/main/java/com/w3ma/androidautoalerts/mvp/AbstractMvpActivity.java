package com.w3ma.androidautoalerts.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.w3ma.androidautoalerts.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by Emanuele on 11/06/2017.
 */

public abstract class AbstractMvpActivity<P extends MvpPresenter> extends AppCompatActivity implements MvpView {

    @Inject
    protected P presenter;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Override
    public void showLoading() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(final int messageResId) {
        showMessage(getString(messageResId));
    }

    @Override
    public void showMessage(final int messageResId, final String... parameters) {
        //it-s working
        showMessage(getString(messageResId, parameters));
    }

    @Override
    public void showMessage(final String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        inject();
        presenter.onCreate(getIntent().getExtras());
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    /**
     * The activity knows what must be injected
     */
    protected abstract void inject();

    /**
     * The activity knows the layout to be displayed
     *
     * @return the layout id
     */
    protected abstract int getLayout();

    @Override
    public void finishActivity() {
        finish();
    }
}
