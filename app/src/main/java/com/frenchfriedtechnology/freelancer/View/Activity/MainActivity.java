package com.frenchfriedtechnology.freelancer.View.Activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.R;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    UserPrefs userPrefs = new UserPrefs();

    /*private ImageView logo;*/
    private boolean isSearchResultView = false;
    static final int PICK_CONTACT_REQUEST = 69;
    private String contactID;

    @Bind(R.id.activity_main_layout_container)
    CoordinatorLayout mRlContainer;
    @Bind(R.id.content_main_update_log_image)
    ImageView logo;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userPrefs.getUser() == null || userPrefs.getUser().equals("")) {
            Log.d(Logger.TAG, "Nothing Saved, Opening Setup");
            gotoSetup();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_freelancer);
        toolbar.setTitle(userPrefs.getUser());
        setSupportActionBar(toolbar);
 /*
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
*/
       /* logo = (ImageView) findViewById(R.id.user_logo);
        if (userPrefs.getUserLogo()!=null){
            logo.setVisibility(View.VISIBLE);
           *//* Drawable drawable = Drawable.createFromPath(userPrefs.getUserLogo());
            logo.setAdjustViewBounds(true);
            toolbar.setLogo(drawable);*//*
            Glide.with(this).load(userPrefs.getUserLogo()).into(logo);
        }
        else {
            logo.setVisibility(View.GONE);
        }*/


    }

    /**
     * Sends the user "Home" if not already there,
     * otherwise creates confirmation dialog before exiting
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Dialog_Theme));
        builder.setTitle(R.string.dialog_exit)
                .setMessage(getString(R.string.dialog_exit_question))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onStop();
                        finish();
                    }
                }).show();
    }


    public void gotoSetup() {
        Intent intent = new Intent(this, SetupActivity.class);
/*
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
*/
        startActivity(intent);
    }

    public void createNewClient(View view) {
        //Create new ClientClient
        Intent intent = ClientList.startClientListNewClient(MainActivity.this, true);
        startActivity(intent);
/*
        CreateClientDialog.newInstance(null,CreateClientDialog.NEW_CLIENT).show(getSupportFragmentManager(), null);
*/

    }

    public void updateDailyLog(View view) {
        //Open Daily Log Activity
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,logo,"update log");
        startActivity(new Intent(this,UpdateLog.class),optionsCompat.toBundle());
  /*      Intent intent = new Intent(this, UpdateLog.class);
        startActivity(intent);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
*/
    }

    public void openSettings(View view) {
        //goto settings Activity
        gotoSetup();
    }

    public void monthlyTally(View view) {
        //Open Monthly Tally Dialog
        Intent intent = new Intent(this, Test.class);
/*
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
*/
        startActivity(intent);
    }

    public void yearlyTally(View view) {
        //Open Yearly Tally Dialog;
        Intent intent = new Intent(this, YearlyTotalActivity.class);
        startActivity(intent);

    }

    public void viewClientList(View view) {
        //Open Dialog of Clients
        Intent intent = ClientList.startClientListNewClient(MainActivity.this, false);
        startActivity(intent);
    }

    public void timedSession(View view) {
        //edit client list
        Intent intent = new Intent(this, TimedSession.class);
        startActivity(intent);
        Toast.makeText(this, "Open Timed Session", Toast.LENGTH_SHORT).show();
    }


}
