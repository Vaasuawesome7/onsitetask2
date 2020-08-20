package com.example.mysms;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "yes";

    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber = null, message = null;
        System.out.println("reached");
        Log.d(TAG, "onReceive");
        if (intent.getExtras() != null) {
            phoneNumber = intent.getExtras().getString("ph");
            message = intent.getExtras().getString("msg");

            System.out.println("second " + phoneNumber + " " + message);
        }
        if (phoneNumber == null || phoneNumber.length() == 0 || message == null || message.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS, context)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show();
            System.out.println("third " + phoneNumber + " " + message);
        }
        else {
            Toast.makeText(context, "Message not sent :(", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkPermission(String perm, Context context) {
        int check = ContextCompat.checkSelfPermission(context, perm);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}
