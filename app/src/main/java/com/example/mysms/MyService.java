package com.example.mysms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static com.example.mysms.App.CHANNEL_ID;

public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phoneNumber = null, message = null;
        int hour = 0, min = 0;
        if (intent.getExtras() != null) {
            phoneNumber = intent.getExtras().getString("ph");
            message = intent.getExtras().getString("msg");
            min = intent.getExtras().getInt("min");
            hour = intent.getExtras().getInt("hr");
        }

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("msg", message);
        notifyIntent.putExtra("ph", phoneNumber);
        notifyIntent.putExtra("h", hour);
        notifyIntent.putExtra("m", min);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Message")
                .setContentText("Sending message " + message + " to " + phoneNumber)
                .setSmallIcon(R.drawable.ic_message)
                .setContentIntent(pendingIntent1)
                .build();

        startForeground(1, notification);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlertReceiver.class);
        i.putExtra("ph", phoneNumber);
        i.putExtra("msg", message);
        System.out.println(phoneNumber);
        System.out.println(message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
