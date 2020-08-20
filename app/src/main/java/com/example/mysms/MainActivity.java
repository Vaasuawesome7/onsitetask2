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
    private int mHour, mMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ph = findViewById(R.id.ph);
        msg = findViewById(R.id.msg);
        time = findViewById(R.id.time_text);
        c = Calendar.getInstance();

        if (getIntent().getExtras() != null) {
            String ph1 = getIntent().getExtras().getString("ph");
            String msg1 = getIntent().getExtras().getString("msg");
            int h = getIntent().getExtras().getInt("h");
            int m = getIntent().getExtras().getInt("m");
            setMainText(h, m);
            ph.setText(ph1);
            msg.setText(msg1);
        }
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
        setMainText(hourOfDay, minute);
        mHour = hourOfDay;
        mMin = minute;
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
        Intent service = new Intent(this, MyService.class);
        service.putExtra("ph", phoneNumber);
        service.putExtra("msg", message);
        service.putExtra("hr", mHour);
        service.putExtra("min", mMin);
        startService(service);
    }

    public void setMainText(int h, int m) {
        String text = "The message will be sent at: " + h + " hour(s)" + ": " + m + " minute(s)";
        if (m<10)
            text = "The message will be sent at: " + h + " hour(s)" + ": 0" + m + " minute(s)";

        if (h<10)
            text = "The message will be sent at: 0" + h + " hour(s)" + ": " + m + " minute(s)";

        time.setText(text);
    }
}