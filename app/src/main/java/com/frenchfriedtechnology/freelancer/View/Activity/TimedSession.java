package com.frenchfriedtechnology.freelancer.View.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.frenchfriedtechnology.freelancer.Common.CircularAnimUtil;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.Events.NamesChosenEvent;
import com.frenchfriedtechnology.freelancer.Freelancer;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.Realm.Client;
import com.frenchfriedtechnology.freelancer.View.Dialog.DialogSelectClients;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * This activity allows user to start and save a timed session for a client and calculates the amount
 * to charge. ~~~Also, options to generate a bill pdf/email?~~
 */
public class TimedSession extends BaseActivity {
    private static final String ACTIVITY_TAG = "TimedSession";
    @Bind(R.id.root)
    CoordinatorLayout root;
    @Bind(R.id.recording_text)
    TextView recordingText;
    @Bind(R.id.button_start_session)
    LinearLayout startButton;
    @Bind(R.id.button_save_session)
    LinearLayout saveButton;
    @Bind(R.id.session_start_image)
    ImageView startImage;
    @Bind(R.id.session_start_text)
    TextView startText;
    @Bind(R.id.session_chronometer)
    Chronometer chronometer;
    @Bind(R.id.session_date)
    TextView sessionDate;
    @Bind(R.id.session_select_client)
    TextView sessionClient;
    @Bind(R.id.session_rate)
    TextView sessionRate;

    private String format = "MM/dd/yy";
    private SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
    private UserPrefs userPrefs = new UserPrefs();
    private Calendar currentDate = Calendar.getInstance();
    private boolean sessionActive = false;
    private long timeWhenStopped = 0;
    private long duration = 200;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_timed_session;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionDate.setText(sdf.format(currentDate.getTime()));

    }


    public void startSession(View view) {
        //start pause session
        if (sessionClient.getText().equals("")) {
            onClickSelectClient();
        } else if (!sessionActive) {
            CircularAnimUtil.enterReveal(recordingText);
            chronometer.setBase(SystemClock.elapsedRealtime()
                    - userPrefs.getTimedSessionCurrentRecordedTime());
            chronometer.start();
 /*           CircularAnimUtil.switchReveal(startImage, pauseImage);
*/
            startImage.setImageResource(R.drawable.vector_pause);

            startText.setText("Pause");
            sessionActive = true;
            userPrefs.setTimedSessionRunning(sessionActive);
        } else {
            CircularAnimUtil.exitReveal(recordingText);
            chronometer.stop();
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
  /*          CircularAnimUtil.switchReveal(pauseImage, startImage);
*/
            startImage.setImageResource(R.drawable.vector_play);

            startText.setText("Play");
            sessionActive = false;
            long positiveTime = Math.abs(timeWhenStopped);
            userPrefs.setTimedSessionCurrentRecordedTime(positiveTime);
            userPrefs.setTimedSessionRunning(sessionActive);
        }
    }

    @OnLongClick(R.id.button_start_session)
    public boolean onResetTimer() {
        //clear chronometer
        if (sessionClient.getText().equals("")) {
            onClickSelectClient();
        }
        if (sessionActive) {
            sessionActive = false;

            startImage.setImageResource(R.drawable.vector_play);
            startText.setText("Play");
        }
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
        CircularAnimUtil.exitReveal(recordingText);
        userPrefs.setTimedSessionPauseTime(timeWhenStopped);
        userPrefs.setTimedSessionCurrentRecordedTime(timeWhenStopped);
        userPrefs.setTimedSessionRunning(sessionActive);
        Snackbar.make(root, "Reset", Snackbar.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Opens Date picker Dialog to choose date
     */

    @OnClick(R.id.session_date)
    void onChooseDate() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                sessionDate.setText(sdf.format(newDate.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.session_select_client)
    void onClickSelectClient() {
        //open select client dialog and populate rate field with selection
        DialogSelectClients.newInstance(DialogSelectClients.SINGLE).show(getSupportFragmentManager(), null);

    }

    public void saveSession(View view) {
        //save session
        Snackbar.make(root, "save Session", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        chronometer.stop();
        if (sessionActive) timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        long positiveTime = Math.abs(timeWhenStopped);
        userPrefs.setTimedSessionCurrentRecordedTime(positiveTime);

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        userPrefs.setTimedSessionPauseTime(currentTime);
        Log.d(Logger.TAG, ACTIVITY_TAG + " onPause pausedSessionTime: " + userPrefs.getTimedSessionCurrentRecordedTime());

        Log.d(Logger.TAG, ACTIVITY_TAG + " onPause() timeWhenStopped: " + positiveTime);
    }

    @Override
    protected void onResume() {
        startText.setText(userPrefs.isTimedSessionRunning() ?
                "Pause" :
                "Play");
        startImage.setImageResource(userPrefs.isTimedSessionRunning() ?
                R.drawable.vector_pause :
                R.drawable.vector_play);
        super.onResume();
        long pausedTime = userPrefs.getTimedSessionPauseTime();
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        long recordedTime = userPrefs.getTimedSessionCurrentRecordedTime();
        long elapsedTimeDifference = currentTime - pausedTime;
        long newTime = recordedTime + elapsedTimeDifference;
        if (userPrefs.isTimedSessionRunning()) {
            sessionActive = true;
            sessionClient.setText(userPrefs.getTimedSessionClient());
            sessionRate.setText(userPrefs.getTimedSessionClientRate());
            chronometer.setBase(SystemClock.elapsedRealtime() - newTime);
            chronometer.start();
            recordingText.post(new Runnable() {
                @Override
                public void run() {
                    CircularAnimUtil.enterReveal(recordingText);
                }
            });
        } else {

            chronometer.setBase(SystemClock.elapsedRealtime() - recordedTime);
            timeWhenStopped = recordedTime;
        }
        Log.d(Logger.TAG, ACTIVITY_TAG + "onResume elapsedTimeDifference: "
                + elapsedTimeDifference
                + "\n recordedTime: "
                + recordedTime + "\n newTime: "
                + newTime);
    }
/*
    private void enterReveal(View view) {
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void exitReveal(final View view) {
        Log.d(Logger.TAG, ACTIVITY_TAG + " exitReveal() ");

        // get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

// get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

// create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);

// make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

// start the animation
        anim.start();
    }*/

    @Subscribe
    public void onNamesChosenEvent(NamesChosenEvent event) {
        sessionClient.setText(event.getNames());
        userPrefs.setTimedSessionClient(event.getNames());
        sessionRate.setText(event.getRate());
        userPrefs.setTimedSessionClientRate(event.getRate());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
