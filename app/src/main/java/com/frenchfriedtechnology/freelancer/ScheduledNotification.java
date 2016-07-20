package com.frenchfriedtechnology.freelancer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.View.Activity.MainActivity;
import com.frenchfriedtechnology.freelancer.View.Activity.UpdateLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by matteo on 30/06/16.
 */
public class ScheduledNotification extends Job {

    public static final String TAG = "FreelancerJob";

    public static void schedule() {
        schedule(true);
    }

    private static void schedule(boolean updateCurrent) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        long notifyTime = new UserPrefs().getNotifyTime();
        long notifyTimeEnd = notifyTime + TimeUnit.HOURS.toMillis(1);
        //window to notify
        if (calendar.getTimeInMillis()>notifyTime){
            notifyTime = calendar.getTimeInMillis()-notifyTime;
        }else{
            notifyTime = notifyTime-calendar.getTimeInMillis();
        }
        // 1 AM - 6 AM, ignore seconds
        long startMs = TimeUnit.MINUTES.toMillis(60 - minute)
                + TimeUnit.HOURS.toMillis((24 - hour) % 24);
        long endMs = startMs + TimeUnit.HOURS.toMillis(5);


        new JobRequest.Builder(TAG)
                .setExecutionWindow(notifyTime, notifyTimeEnd)
                .setPersisted(true)
                .setUpdateCurrent(updateCurrent)
                .build()
                .schedule();
    }


    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(Logger.TAG,"ScheduledNotifications onRunJob()");
        try {
            Context context = getContext();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);
            Log.d(Logger.TAG, "Day of week = " + dayOfTheWeek);
            if (new UserPrefs().getNotifyFrequency().contains(dayOfTheWeek)) {

                //make notification
                Intent resultIntent = new Intent(context, UpdateLog.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.freelancer_notification)
                                .setContentTitle(new UserPrefs().getUser())
                                .setContentText(context.getResources().getString(R.string.notification_content))
                                .addAction(R.drawable.ic_done, "Daily Log", resultPendingIntent)// accept notification button
                                .setDefaults(android.app.Notification.DEFAULT_SOUND)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setAutoCancel(true);

                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }

            // do something
            return Result.SUCCESS;
        } finally {
            schedule(false); // don't update current, it would cancel this currently running job
        }
    }
}
