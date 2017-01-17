package com.scanba.remindme.services;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.scanba.remindme.DatabaseHelper;
import com.scanba.remindme.R;
import com.scanba.remindme.models.Reminder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RemindMeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        final Context context = this;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Dao<Reminder, Integer> reminderDao = databaseHelper.getReminderDao();
                    QueryBuilder<Reminder, Integer> queryBuilder = reminderDao.queryBuilder();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MINUTE, 10);
                    List<Reminder> reminders = reminderDao.query(queryBuilder.where()
                            .lt("remindAt", new Date(cal.getTimeInMillis()))
                            .prepare());
                    if(reminders.size() > 0) {
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        for(Reminder reminder : reminders) {
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                            mBuilder.setSmallIcon(R.drawable.calendar_clock);
                            mBuilder.setContentTitle("Reminder");
                            mBuilder.setContentText(reminder.getDescription());
                            mBuilder.setSound(uri);
                            mBuilder.setLights(Color.WHITE, 1000, 5000);
                            mNotificationManager.notify(911991, mBuilder.build());
                            reminder.delete(reminderDao);
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        return Service.START_STICKY;
    }

    public static void start(Context context) {
        if(!isRunning(context)) {
            Intent intent = new Intent(context, RemindMeService.class);
            PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
            AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 420000, 420000, pIntent);
            markAsRunning(context);
        }
    }

    public static boolean isRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("RemindMePref", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isReminderServiceRunning", false))
            return true;
        else
            return false;
    }

    public static void markAsRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("RemindMePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isReminderServiceRunning", true);
        editor.commit();
    }
}
