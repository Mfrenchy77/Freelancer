package com.frenchfriedtechnology.freelancer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.frenchfriedtechnology.freelancer.Common.Logger;
import com.frenchfriedtechnology.freelancer.Common.UserPrefs;
import com.frenchfriedtechnology.freelancer.View.Activity.MainActivity;
import com.frenchfriedtechnology.freelancer.View.Activity.UpdateLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This receives the Alarm Manger wake up call from the {@link
 * com.frenchfriedtechnology.freelancer.View.Activity.SetupActivity}
 * Creates and pending intent to Open {@link UpdateLog}.
 *
 * Also, checks the {@link UserPrefs} to see if User Requested a
 * notification on this day
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Logger.TAG, "Received Broadcast");
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

    }
}
