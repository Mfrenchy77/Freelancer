package com.frenchfriedtechnology.freelancer.View.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.Common.CircularAnimUtil;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.R;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    public static final int ANIMATION_DURATION = 800;
    public static final String ACTIVITY_TAG = "MainActivity";
    private static final String ANIMATION_STATE = "ANIMATION_STATE";
    UserPrefs userPrefs = new UserPrefs();

    /*private ImageView logo;*/
    private boolean isSearchResultView = false;
    static final int PICK_CONTACT_REQUEST = 69;
    private String contactID;

    private boolean buttonsAreHidden = true;

    @Bind(R.id.button_update_log)
    LinearLayout centerButton;
    @Bind(R.id.button_create_client)
    LinearLayout createClientButton;
    @Bind(R.id.button_view_client_list)
    LinearLayout clientListButton;
    @Bind(R.id.button_timed_session)
    LinearLayout timedSessionButton;
    @Bind(R.id.button_yearly_tally)
    LinearLayout yearlyButton;
    @Bind(R.id.button_monthly_tally)
    LinearLayout monthlyButton;
    @Bind(R.id.button_settings)
    LinearLayout settingsButton;


    @Bind(R.id.activity_main_layout_container)
    CoordinatorLayout root;


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
       /* initViews();
        root.post(new Runnable() {
            @Override
            public void run() {

                enterAnimation();
            }
        });*/
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
        }
        hideViews();*/

    }

    private void hideViews() {
        centerButton = (LinearLayout) findViewById(R.id.button_update_log);
        createClientButton = (LinearLayout) findViewById(R.id.button_create_client);
        clientListButton = (LinearLayout) findViewById(R.id.button_view_client_list);
        timedSessionButton = (LinearLayout) findViewById(R.id.button_timed_session);
        settingsButton = (LinearLayout) findViewById(R.id.button_settings);
        monthlyButton = (LinearLayout) findViewById(R.id.button_monthly_tally);
        yearlyButton = (LinearLayout) findViewById(R.id.button_yearly_tally);
        createClientButton.setVisibility(View.INVISIBLE);
        clientListButton.setVisibility(View.INVISIBLE);
        timedSessionButton.setVisibility(View.INVISIBLE);
        settingsButton.setVisibility(View.INVISIBLE);
        monthlyButton.setVisibility(View.INVISIBLE);
        yearlyButton.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        root.post(new Runnable() {
            @Override
            public void run() {

                enterAnimation();
            }
        });
    }

    /**
     * Sends the user "Home" if not already there,
     * otherwise creates confirmation dialog before exiting
     */
    @Override
    public void onBackPressed() {
        AlertDialog alertDialog;
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
                });

        alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.anim.scale_up;
        alertDialog.show();
    }


    public void gotoSetup() {
        Intent intent = new Intent(this, SetupActivity.class);
/*
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
*/
    }

    public void createNewClient(View view) {
        //Create new ClientClient
        Intent intent = ClientList.startClientListNewClient(MainActivity.this, true);
        pulseAnimation(view, null, intent);

    }

    private void enterAnimation() {
       /* CircularAnimUtil.show(yearlyButton);
        CircularAnimUtil.show(monthlyButton);
        CircularAnimUtil.show(settingsButton);
        CircularAnimUtil.show(timedSessionButton);
        CircularAnimUtil.show(clientListButton);
        CircularAnimUtil.show(createClientButton);
        CircularAnimUtil.show(centerButton);*/

        Log.d(Logger.TAG, ACTIVITY_TAG + " enterAnimation() buttonsHidden: " + buttonsAreHidden);
        if (buttonsAreHidden) {
            CircularAnimUtil.enterReveal(root);
   /*          hideViews();
            int delay = 0;
            enterReveal(yearlyButton, delay * 2);
            enterReveal(monthlyButton, delay + 30 * 2);
            enterReveal(settingsButton, delay + 60 * 2);
            enterReveal(timedSessionButton, delay + 90 * 2);
            enterReveal(clientListButton, delay + 120 * 2);
            enterReveal(createClientButton, delay + 150 * 2);

            AnimatorSet enter = new AnimatorSet();
            enter.play(enterReveal(yearlyButton)).before(enterReveal(monthlyButton));
            enter.play(enterReveal(monthlyButton)).before(enterReveal(settingsButton));
            enter.play(enterReveal(settingsButton)).before(enterReveal(timedSessionButton));
            enter.play(enterReveal(timedSessionButton)).before(enterReveal(clientListButton));
            enter.play(enterReveal(clientListButton)).before(enterReveal(createClientButton));
*/
            buttonsAreHidden = false;
        }
    }

    private void exitAnimation(View touchedView) {/*
        CircularAnimUtil.hide(createClientButton);
        CircularAnimUtil.hide(clientListButton);
        CircularAnimUtil.hide(timedSessionButton);
        CircularAnimUtil.hide(settingsButton);
        CircularAnimUtil.hide(monthlyButton);
        CircularAnimUtil.hide(yearlyButton);*/
        Log.d(Logger.TAG, ACTIVITY_TAG + " exitAnimation() buttonsHidden: " + buttonsAreHidden);
        if (!buttonsAreHidden) {
            CircularAnimUtil.hideOther(touchedView, root);
    /*        int delay = 0;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(exitReveal(createClientButton, delay * 2))
                    .with(exitReveal(clientListButton, delay + 30 * 2))
                    .with(exitReveal(timedSessionButton, delay + 60 * 2))
                    .with(exitReveal(settingsButton, delay + 90 * 2))
                    .with(exitReveal(monthlyButton, delay + 120 * 2))
                    .with(exitReveal(yearlyButton, delay + 150 * 2));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                }
            });
            animatorSet.start();

            AnimatorSet set = new AnimatorSet();
            set.play(animatorSet);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });
            set.start();*/

            buttonsAreHidden = true;
        }
    }

    public void updateDailyLog(View view) {
        //Open Daily Log Activity
        pulseAnimation(centerButton, UpdateLog.class, null);
       /*  Intent intent = new Intent(this, UpdateLog.class);

        exitAnimation(intent);
        pulseAnimation(centerButton);

      ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, centerButton, "update_log");
        startActivity(intent, optionsCompat.toBundle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/


    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);

        Slide slide = new Slide(Gravity.LEFT);
        slide.setInterpolator(new FastOutSlowInInterpolator());
        slide.setDuration(500);

        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(slide);
        getWindow().setReenterTransition(slide);
    }

    private void pulseAnimation(final View view, final Class<?> newClass, final Intent intent) {
        exitAnimation(view);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(200);
        scaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (intent != null) {
                    CircularAnimUtil.startActivity(MainActivity.this, intent, view, R.color.accent);
                } else {
                    CircularAnimUtil.startActivity(MainActivity.this, newClass, view, R.color.accent);
                }
            }
        });
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setRepeatCount(1);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

    }

    private Animator exitReveal(final View view, int delay) {
        Log.d(Logger.TAG, ACTIVITY_TAG + " exitReveal() delay: " + delay);

        // get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

// get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

// create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(ANIMATION_DURATION);
        anim.setStartDelay(delay);

// make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

// start the animation

        return anim;
    }

    private Animator enterReveal(final View view, int delay) {
        Log.d(Logger.TAG, ACTIVITY_TAG + " enterReveal() delay: " + delay);
        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setStartDelay(delay);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(new AccelerateInterpolator());
        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();

        return anim;
    }

    public void openSettings(View view) {
        //goto settings Activity
        pulseAnimation(view, SetupActivity.class, null);
        gotoSetup();
    }

    public void monthlyTally(View view) {
        //Open Monthly Tally Dialog
        Intent intent = new Intent(this, Test.class);

        pulseAnimation(view, Test.class, null);
  /*      exitAnimation(intent);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
*/
    }

    public void yearlyTally(View view) {
        //Open Yearly Tally Dialog;
        pulseAnimation(view, YearlyTotalActivity.class, null);
  /*      Intent intent = new Intent(this, YearlyTotalActivity.class);
        exitAnimation(intent);

        startActivity(intent);
*/

    }

    public void viewClientList(View view) {
        //Open Dialog of Clients
        pulseAnimation(view, ClientList.class, null);
 /*       Intent intent = ClientList.startClientListNewClient(MainActivity.this, false);
        exitAnimation(intent);

        startActivity(intent);
*/
    }

    public void timedSession(View view) {
        //edit client list
        pulseAnimation(view, TimedSession.class, null);
   /*      CircularAnimUtil.startActivity(this, TimedSession.class, timedSessionButton, R.color.accent);
       Intent intent = new Intent(this, TimedSession.class);
        exitAnimation(intent);

        startActivity(intent);
*/
        Toast.makeText(this, "Open Timed Session", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(ANIMATION_STATE, buttonsAreHidden);
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        buttonsAreHidden = savedInstanceState.getBoolean(ANIMATION_STATE);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
