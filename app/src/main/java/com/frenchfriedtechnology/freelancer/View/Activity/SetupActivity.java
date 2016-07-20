package com.frenchfriedtechnology.freelancer.View.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.frenchfriedtechnology.freelancer.AlarmReceiver;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.Database.FreeLancerDatabaseAdapter;
import com.frenchfriedtechnology.freelancer.Events.DayChosenEvent;
import com.frenchfriedtechnology.freelancer.Freelancer;
import com.frenchfriedtechnology.freelancer.R;
import com.frenchfriedtechnology.freelancer.ScheduledNotification;
import com.frenchfriedtechnology.freelancer.View.Dialog.DialogFrequencyPicker;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.squareup.otto.Subscribe;


import java.text.DateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.OnEditorAction;


/**
 * Activity for User to input basic setting for the duration of use: Business Name,
 * Notification Time, Days to be notified, and chooses a logo.
 */
public class SetupActivity extends BaseActivity implements ImageChooserListener {
    private ImageView logo;
    private UserPrefs userPrefs = new UserPrefs();
    private DateFormat fmtDateAndTime = DateFormat.getTimeInstance(DateFormat.SHORT);
    private Calendar notificationTime = Calendar.getInstance();
    private ImageChooserManager imageChooserManager;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String SETUP_TRANSITION_NAME = "SETUP_TRANSITION";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int notificationHour,
                              int notificationMinute) {
            notificationTime.set(Calendar.HOUR_OF_DAY, notificationHour);
            notificationTime.set(Calendar.MINUTE, notificationMinute);
            updateLabel();
            setNotifyFreq();
        }
    };
    @Bind(R.id.notify_time)
    TextView notifyTime;
    @Bind(R.id.notify_freq)
    TextView notifyFreq;
    @Bind(R.id.business_name)
    EditText businessName;
    @Bind(R.id.create_setup_button)
    ImageView createButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    /*
        getWindow().setSharedElementEnterTransition();
// set an exit transition
    getWindow().setExitTransition(new ChangeBounds());*/
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_freelancer);
        toolbar.setTitle(R.string.setup_name);
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE);
     /*   ActivityCompat.requestPermissions(this, PERMISSION_CONTACTS, REQUEST_READ_CONTACTS);

        logo = (ImageView) findViewById(R.id.user_logo);*/

        if (userPrefs.getUser() != null) {
            businessName.setText(userPrefs.getUser());
        }
        if (userPrefs.getNotifyTime() != 0) {
            Log.d(Logger.TAG, "User Prefs Notify Time: " + userPrefs.getNotifyTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(userPrefs.getNotifyTime());
            notifyTime.setText(fmtDateAndTime.format(calendar.getTime()));
        }
        if (userPrefs.getNotifyFrequency() != null) {
            notifyFreq.setText(userPrefs.getNotifyFrequency());
        }
        /*if (userPrefs.getUserLogo() != null) {
            logo.setVisibility(View.VISIBLE);
            Glide.with(this).load(userPrefs.getUserLogo()).into(logo);
        }*/
        createButton.setTransitionName(SETUP_TRANSITION_NAME);
    }

    /**
     * Opens Time Picker when user is finished entering their Business name
     */
    @OnEditorAction(R.id.business_name)
    boolean onFinishedEnteringName() {
        Freelancer.hideSoftKeyboard(this);
        return true;
    }

    public void doSomethingCool() {
        //this is where we do something cool

    }

    /**
     * ImageView is clicked check to see if text and setup is complete, save
     * info in shared prefs and sends user to main activity or sends back to
     * business name EditText
     */
    public void setUpUser(View view) {
        if (!TextUtils.isEmpty(businessName.getText())) {
            userPrefs.setUser(businessName.getText().toString());
            if (notifyTime != null && !notifyTime.equals(userPrefs.getNotifyTime())) {

                scheduleNotifications();
            }
            Log.d(Logger.TAG, "UserPrefs Notify Time: " + userPrefs.getNotifyTime());
            gotoMain();
        } else {
            Toast.makeText(this, "No Name Entered", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ImageView to pick the days to be notified to update Daily Log
     */
    public void setUpFrequency(View view) {
        setNotifyFreq();
    }

    /**
     * ImageView to open Time Picker
     */
    public void setUpNotifyTime(View view) {
        setUpNotifyTime();
    }

  /*  private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }
*/

    /**
     * ImageView to open Image Chooser
     */
    public void chooseLogo(View view) throws ChooserException {
        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.choose();
        Toast.makeText(this, "Pick a Logo", Toast.LENGTH_SHORT).show();
    }

    /**
     * ImageView to clear all user settings after confirmation dialog
     */
    public void clearSettings(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Dialog_Theme));
        builder.setTitle(R.string.dialog_clear_setup_title)
                .setMessage(getString(R.string.dialog_clear_setup))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        businessName.setText("");
                        notifyTime.setText("");
                        notifyFreq.setText("");
/*
                        logo.setVisibility(View.GONE); //fixme do something with the logo

*/
                        userPrefs.clearUser();
                        userPrefs.clearNotifyFrequency();
                        userPrefs.setUserLogo("");
                        userPrefs.setUser("");
                        userPrefs.setNotifyTime(0);
                        //// TODO: 30/06/16 change this to clear realm db
                        FreeLancerDatabaseAdapter adapter = new FreeLancerDatabaseAdapter(Freelancer.getContext());
                        adapter.clearTables();
                    }
                }).show();
    }

    /**
     * Method to open multi-choice selector for days of the week
     */
    public void setNotifyFreq() {
        DialogFrequencyPicker.newInstance(R.string.setup_notify_freq_title,
                DialogFrequencyPicker.CHOSE_DAYS_FOR_NOTIFICATION, null).show(getSupportFragmentManager(), null);
    }

    /**
     * Open Time Picker and save the time to send Notification to update Daily Log
     */
    private void setUpNotifyTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                timeSetListener,
                notificationTime.get(Calendar.HOUR_OF_DAY),
                notificationTime.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    /**
     * Update TextViews from Time Picker
     */
    private void updateLabel() {
        notifyTime.setText(fmtDateAndTime.format(notificationTime.getTime()));
        userPrefs.setNotifyTime(notificationTime.getTimeInMillis());
    }

    /**
     * Result from user picking an Image from their device
     */
    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    // Use the image
                    userPrefs.setUserLogo(image.getFilePathOriginal());
                   /* Log.d(Logger.TAG, "Image = " + image);
                    Log.d(Logger.TAG, "Image extension = " + image.getExtension());
                    logo.setVisibility(View.VISIBLE);
                    image.getFilePathOriginal();
                    Glide.with(Freelancer.getContext()).load(userPrefs.getUserLogo()).into(logo);*/
                    // image.getFileThumbnail();
                    // image.getFileThumbnailSmall();
                }
            }
        });
    }

    @Override
    public void onError(String s) {
        Log.d(Logger.TAG, "Logo getter ERROR = " + s);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK &&
                (requestCode == ChooserType.REQUEST_PICK_PICTURE ||
                        requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            imageChooserManager.submit(requestCode, data);
        }
    }


    /**
     * Method called in the Time Picker Listener at the top. Sets the time to wake up
     * and receive a notification if the scheduled for that day
     */
    public void scheduleNotifications() {
        Log.d(Logger.TAG, "scheduleNotifications()");
        //Create alarm manager

        ScheduledNotification.schedule();

       /* AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int notificationHour = notificationTime.get(Calendar.HOUR_OF_DAY);
        int notificationMinute = notificationTime.get(Calendar.MINUTE);
        Log.d(Logger.TAG, "NOTIFY HOUR: " + notificationHour);
        Log.d(Logger.TAG, "NOTIFY MIN: " + notificationMinute);

        //Create pending intent & register it to alarm notifier class
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //set timer you for alarm to work

        DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        calendar.setTimeInMillis(userPrefs.getNotifyTime());
        Log.d(Logger.TAG, "time to trigger = " + fmtDateAndTime.format(calendar.getTime()));
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, notificationHour);
        calendar.set(Calendar.MINUTE, notificationMinute);
        calendar.set(Calendar.SECOND, 0);

        Log.d(Logger.TAG, "trigger date  = " + fmtDateAndTime.format(calendar.getTime()));
        //set that timer as a RTC Wakeup to alarm manager object
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, userPrefs.getNotifyTime(), AlarmManager.INTERVAL_DAY, pendingIntent);*/
    }

    /**
     * Days of week received from Event Bus, insert into TextViews
     */
    @Subscribe
    public void daysChosenEvent(DayChosenEvent event) {
        if (DialogFrequencyPicker.CHOSE_DAYS_FOR_NOTIFICATION.equals(event.getTag())) {
            Log.d(Logger.TAG, "BUS FOR - " + event.getTag() + "\n" + event.getText());
            userPrefs.setNotifyFrequency(event.getText());
            notifyFreq.setText(userPrefs.getNotifyFrequency());
        }
    }

    /**
     * Confirms exit of app
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

    /**
     * Method sending user to Main Activity
     */
    public void gotoMain() {

        // TODO: 30/06/16 add shared element and window transitions maybe with circular reveal?
        int x = createButton.getLeft();
        int y = createButton.getTop();
        int height = createButton.getHeight();
        int width = createButton.getWidth();
        Log.d(Logger.TAG, "Starting Freelancer for " + new UserPrefs().getUser());
        Intent intent = new Intent(this, MainActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, createButton, SETUP_TRANSITION_NAME);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent, options.toBundle());
    }

}
