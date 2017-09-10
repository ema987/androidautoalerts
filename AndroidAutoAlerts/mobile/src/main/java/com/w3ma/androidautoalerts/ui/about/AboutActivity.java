package com.w3ma.androidautoalerts.ui.about;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.w3ma.androidautoalerts.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.authorInfoTextView)
    TextView authorInfoTextView;
    @BindView(R.id.iconsLicenseTextView)
    TextView iconsLicenseTextView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        authorInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        iconsLicenseTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
