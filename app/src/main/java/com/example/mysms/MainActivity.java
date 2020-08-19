package com.example.mysms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText ph, msg;
    private TextView time;
    private final int REQ_CODE = 1;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ph = findViewById(R.id.ph);
        msg = findViewById(R.id.msg);
        time = findViewById(R.id.time_text);
        c = Calendar.getInstance();

        if (!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQ_CODE);
        }

    }

    public void set(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String text = "The message will be sent at: " + hourOfDay + " hour(s)" + ": " + minute + " minute(s)";
        if (minute<10)
            text = "The message will be sent at: " + hourOfDay + " hour(s)" + ": 0" + minute + " minute(s)";

        if (hourOfDay<10)
            text = "The message will be sent at: 0" + hourOfDay + " hour(s)" + ": " + minute + " minute(s)";

        time.setText(text);

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
    }

    public boolean checkPermission(String perm) {
        int check = ContextCompat.checkSelfPermission(this, perm);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public void sendNow(View view) {
        String phoneNumber = ph.getText().toString();
        String message = msg.getText().toString();
        if (phoneNumber == null || phoneNumber.length() == 0 || message == null || message.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Message not sent :(", Toast.LENGTH_SHORT).show();
        }
    }

    public void confirm(View view) {
        String phoneNumber = ph.getText().toString();
        String message = msg.getText().toString();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("ph", phoneNumber);
        intent.putExtra("msg", message);
        System.out.println(phoneNumber);
        System.out.println(message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}