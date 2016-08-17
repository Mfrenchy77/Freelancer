package com.frenchfriedtechnology.freelancer.View.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup;
    }

    @Bind(R.id.setup_root)
    CoordinatorLayout root;
    @Bind(R.id.notify_time)
    TextView notifyTime;
    @Bind(R.id.notify_freq)
    TextView notifyFreq;
    @Bind(R.id.business_name)
    EditText businessName;
    @Bind(R.id.create_setup_button)
    ImageView createButton;
/*

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

*/

    private ImageChooserManager imageChooserManager;
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int notificationHour,
                              int notificationMinute) {

            notificationTime.set(Calendar.HOUR_OF_DAY, notificationHour);
            notificationTime.set(Calendar.MINUTE, notificationMinute);

            updateLabel();
            setNotifyFreq();
        }
    };
    private DateFormat fmtDateAndTime = DateFormat.getTimeInstance(DateFormat.SHORT);
    private Calendar notificationTime = Calendar.getInstance();
    private ImageView logo; // TODO: 17/08/16 currently deactivated
    private UserPrefs userPrefs = new UserPrefs();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_freelancer);
        toolbar.setTitle(R.string.setup_name);

        isStoragePermissionGranted();

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
    }

    /**
     * Check for storage Permissions and handle result
     */
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(Logger.TAG, "Permission is granted");
                return true;
            } else {

                Log.d(Logger.TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d(Logger.TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.d(Logger.TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    /**
     * Opens Time Picker when user is finished entering their Business name
     */
    @OnEditorAction(R.id.business_name)
    boolean onFinishedEnteringName() {
        Freelancer.hideSoftKeyboard(this);
        return true;
    }


    /**
     * ImageView is clicked, check to see if text and setup is complete, save
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
            Snackbar.make(root, "Please enter a name", Snackbar.LENGTH_SHORT).show();
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
                        // TODO: 17/08/16   clear realm db
                    }
                }).show();
    }

    /**
     * Method to open multi-choice selector for days of the week
     */
    private void setNotifyFreq() {

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

        ScheduledNotification.schedule();// FIXME: 17/08/16 still not working correctly, notifications aren't always scheduled
    }

    /**
     * Days of week received from Event Bus, insert into TextViews
     */
    @Subscribe
    public void daysChosenEvent(DayChosenEvent event) {

        if (DialogFrequencyPicker.CHOSE_DAYS_FOR_NOTIFICATION.equals(event.getTag())) {
            userPrefs.setNotifyFrequency(event.getText());
            notifyFreq.setText(userPrefs.getNotifyFrequency());
        }
    }


    /**
     * Method sending user to Main Activity
     */
    public void gotoMain() {
        Log.d(Logger.TAG, "Starting Freelancer for " + new UserPrefs().getUser());

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
