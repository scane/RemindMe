package com.scanba.remindme.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.scanba.remindme.DatabaseHelper;
import com.scanba.remindme.R;
import com.scanba.remindme.models.Reminder;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddReminderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText reminderDescription;
    TextView date, time;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    Dao<Reminder, Integer> reminderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
            reminderDao = databaseHelper.getReminderDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        reminderDescription = (EditText) findViewById(R.id.reminder_description);
        date = (TextView) findViewById(R.id.remind_me_date);
        time = (TextView) findViewById(R.id.remind_me_time);
        calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(df.format(calendar.getTime()));
        DateFormat tf = new SimpleDateFormat("HH:mm");
        time.setText(tf.format(calendar.getTime()));

        datePickerDialog = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(this, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
    }

    public void openDatePickerDialog(View v) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            calendar.setTime(df.parse(date.getText().toString()));
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void openTimePickerDialog(View v) {
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        try {
            calendar.setTime(tf.parse(time.getText().toString()));
            timePickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            timePickerDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month +1) + "/" + year);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
    }

    public void createReminder(View v) throws ParseException, SQLException {
        v.setEnabled(false);
        String description = reminderDescription.getText().toString();
        if(description.trim().length() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date remindAt = format.parse(date.getText().toString() + " " + time.getText().toString());
            Reminder reminder = new Reminder(description, remindAt);
            reminderDao.create(reminder);
            Intent i = new Intent(this, RemindersActivity.class);
            startActivity(i);
        }
        else {
            v.setEnabled(true);
            reminderDescription.setError("cant't be blank");
        }


    }
}
