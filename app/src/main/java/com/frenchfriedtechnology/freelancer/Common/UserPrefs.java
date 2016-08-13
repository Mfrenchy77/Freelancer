package com.frenchfriedtechnology.freelancer.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.frenchfriedtechnology.freelancer.Freelancer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by matteo on 1/25/16.
 */
public class UserPrefs {

    private static final String ACTIVITY_TAG = "USER_PREFS";
    private static final String SHARED_PREFS_NOTIFY_TIME = "NOTIFY_TIME";
    private static final String SHARED_PREFS_NOTIFY_FREQ = "NOTIFY_FREQ";
    private static final String SHARED_PREFS_NAME = "FREELANCER";
    private static final String SHARED_PREFS_SETUP = "SETUP";
    private static final String SHARED_PREFS_LOGO = "LOGO";
    private static final String USER_NAME = "USER_NAME";
    private static final String SHARED_PREFS_TIMED_SESSION_RUNNING = "TIMED_SESSION_RUNNING";
    private static final String SHARED_PREFS_TIMED_SESSION_PAUSE_TIME = "PAUSE_TIME";
    private static final String SHARED_PREFS_TIMED_SESSION_CURRENT_RECORDED_TIME = "CURRENT_RECORDED_TIME";
    private static final String SHARED_PREFS_TIMED_SESSION_CLIENT = "TIMED_SESSION_CLIENT";
    private static final String SHARED_PREFS_TIMED_SESSION_CLIENT_RATE = "TIMED_SESSION_CLIENT_RATE";

    public UserPrefs() {
    }

    /**
     * Sets the Users name or Business Name
     */
    public void setUser(String businessName) {
        editSharedPrefs().putString(USER_NAME, businessName).apply();
        editSharedPrefs().putBoolean(SHARED_PREFS_SETUP, true);
    }

    public String getUser() {
        return getSharedPrefs().getString(USER_NAME, null);
    }

    public void clearUser() {
        editSharedPrefs().clear();
    }

    /**
     * Getters and Setters for Timed Session to calculate elapsed time if Freelancer pauses or closes
     */

    public void setTimedSessionRunning(boolean running) {
        Log.d(Logger.TAG, ACTIVITY_TAG + " setTimedSessionRunning() " + running);
        editSharedPrefs().putBoolean(SHARED_PREFS_TIMED_SESSION_RUNNING, running).apply();
    }

    public boolean isTimedSessionRunning() {
        return getSharedPrefs().getBoolean(SHARED_PREFS_TIMED_SESSION_RUNNING, false);
    }

    public void setTimedSessionPauseTime(long pauseTime) {
        Log.d(Logger.TAG, ACTIVITY_TAG + " setTimedSessionPauseTime() pauseTime: " + pauseTime);
        editSharedPrefs().putLong(SHARED_PREFS_TIMED_SESSION_PAUSE_TIME, pauseTime).apply();
    }

    public long getTimedSessionPauseTime() {
        return getSharedPrefs().getLong(SHARED_PREFS_TIMED_SESSION_PAUSE_TIME, 0);
    }

    public void setTimedSessionCurrentRecordedTime(long recordedTime) {
        Log.d(Logger.TAG, ACTIVITY_TAG + " setTimedSessionCurrentElapsedTime() recordedTime: " + recordedTime);
        editSharedPrefs().putLong(SHARED_PREFS_TIMED_SESSION_CURRENT_RECORDED_TIME, recordedTime).apply();
    }

    public long getTimedSessionCurrentRecordedTime() {
        return getSharedPrefs().getLong(SHARED_PREFS_TIMED_SESSION_CURRENT_RECORDED_TIME, 0);
    }

    public void setTimedSessionClient(String client){
        Log.d(Logger.TAG,ACTIVITY_TAG+ "setTimedSessionClient() client: "+client);
        editSharedPrefs().putString(SHARED_PREFS_TIMED_SESSION_CLIENT,client).apply();
    }
    public String getTimedSessionClient(){
        return getSharedPrefs().getString(SHARED_PREFS_TIMED_SESSION_CLIENT,"");
    }

    public void setTimedSessionClientRate(String rate){
        Log.d(Logger.TAG,ACTIVITY_TAG+ " setTimedSessionClientRate() rate: "+ rate);
        editSharedPrefs().putString(SHARED_PREFS_TIMED_SESSION_CLIENT_RATE,rate).apply();
    }
    public String getTimedSessionClientRate(){
        return getSharedPrefs().getString(SHARED_PREFS_TIMED_SESSION_CLIENT_RATE,"");
    }


    /**
     * Saves the days to be notified
     */
    public void setNotifyFrequency(String days) {
        Log.d(Logger.TAG, "List = " + getNotifyFrequency());
        Log.d(Logger.TAG, "day = " + days);
        if (!getNotifyFrequency().contains(days)) {
            editSharedPrefs().putString(SHARED_PREFS_NOTIFY_FREQ, days).apply();
        }
    }

    public String getNotifyFrequency() {
        return getSharedPrefs().getString(SHARED_PREFS_NOTIFY_FREQ, "");
    }

    public void clearNotifyFrequency() {
        editSharedPrefs().remove(SHARED_PREFS_NOTIFY_FREQ).apply();

        Log.d(Logger.TAG, "Cleared Notify Frequency");
    }

    /**
     * Saves the Time that reminders will be sent out
     */
    public void setNotifyTime(long notifyTime) {
        Date date = new Date(notifyTime);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
        Log.d(Logger.TAG, "Notify Time = " + dateFormatted);
        editSharedPrefs().putLong(SHARED_PREFS_NOTIFY_TIME, notifyTime).apply();
    }

    public long getNotifyTime() {
        return getSharedPrefs().getLong(SHARED_PREFS_NOTIFY_TIME, 0);
    }

    /**
     * Saves User's logo
     */
    public void setUserLogo(String logo) {
        Log.d(Logger.TAG, "Logo = " + logo);
        editSharedPrefs().putString(SHARED_PREFS_LOGO, logo).apply();
    }

    public String getUserLogo() {
        Log.d(Logger.TAG, "getUserLogo Called " + getSharedPrefs().getString(SHARED_PREFS_LOGO, null));
        return getSharedPrefs().getString(SHARED_PREFS_LOGO, null);
    }

    /**
     * @return true if user is not null
     */
    public boolean hasSetup() {
        return getSharedPrefs().getBoolean(SHARED_PREFS_SETUP, false);
    }

    /**
     * Primary methods to get and edit Shared Prefs
     */
    private SharedPreferences getSharedPrefs() {
        return Freelancer.getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor editSharedPrefs() {
        return getSharedPrefs().edit();
    }

}
