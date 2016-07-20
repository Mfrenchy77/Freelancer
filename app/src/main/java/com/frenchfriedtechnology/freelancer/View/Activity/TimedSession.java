package com.frenchfriedtechnology.freelancer.View.Activity;

import android.app.DatePickerDialog;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    @Bind(R.id.root)
    CoordinatorLayout root;
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

    private Calendar currentDate = Calendar.getInstance();
    private boolean sessionActive = false;
    private long timeWhenStopped = 0;

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
        if (!sessionActive) {
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chronometer.start();
            startImage.setImageResource(R.drawable.vector_pause);
            startText.setText("Pause");
            sessionActive = true;
        } else {
            chronometer.stop();
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            startImage.setImageResource(R.drawable.vector_play);
            startText.setText("Play");
            sessionActive = false;
        }
    }

    @OnLongClick(R.id.button_start_session)
    public boolean onResetTimer() {
        //clear chronometer
        if (sessionActive) {
            sessionActive = false;

            startImage.setImageResource(R.drawable.vector_play);
            startText.setText("Play");
        }
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
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

    @Subscribe
    public void onNamesChosenEvent(NamesChosenEvent event) {
        sessionClient.setText(event.getNames());
        sessionRate.setText(event.getRate());
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
