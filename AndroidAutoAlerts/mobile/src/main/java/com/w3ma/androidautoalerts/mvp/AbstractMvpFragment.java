package com.w3ma.androidautoalerts.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by Emanuele on 08/06/2017.
 */

public abstract class AbstractMvpFragment<P extends MvpPresenter> extends Fragment implements MvpView {

    @Inject
    protected P presenter;

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
        showMessage(getString(messageResId, new Object[]{parameters}));
    }

    @Override
    public void showMessage(final String message) {
        showContent();
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        } else {
            //fallback, should never happen
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        inject();
        presenter.onCreate(getArguments());
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    /**
     * The fragment knows what must be injected
     */
    protected abstract void inject();

    /**
     * The fragment knows the layout to be displayed
     *
     * @return the layout id
     */
    protected abstract int getLayout();

    @Override
    public void finishActivity() {
        getActivity().finish();
    }
}
