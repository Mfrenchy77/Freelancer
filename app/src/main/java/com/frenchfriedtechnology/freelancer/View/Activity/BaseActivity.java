package com.frenchfriedtechnology.freelancer.View.Activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.frenchfriedtechnology.freelancer.Common.BusProvider;
import com.frenchfriedtechnology.freelancer.R;

import butterknife.ButterKnife;


/**
 * Created by matteo on 2/4/16.
 *
 * Contains boilerplate that is shared across activities e.g. Butterknife injection/Otto Event bus
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected
    @LayoutRes
    abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_freelancer);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        BusProvider.Instance.getBus().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.Instance.getBus().unregister(this);
    }

}