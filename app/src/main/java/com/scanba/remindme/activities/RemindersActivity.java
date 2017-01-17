package com.scanba.remindme.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.scanba.remindme.DatabaseHelper;
import com.scanba.remindme.R;
import com.scanba.remindme.adapters.RemindersAdapter;
import com.scanba.remindme.models.Reminder;
import com.scanba.remindme.services.RemindMeService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Dao<Reminder, Integer> reminderDao;
        List<Reminder> reminders = new ArrayList<>();
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
            reminderDao = databaseHelper.getReminderDao();
            QueryBuilder<Reminder, Integer> queryBuilder = reminderDao.queryBuilder();
            reminders = reminderDao.query(queryBuilder.orderBy("remindAt", true).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reminders_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        RemindersAdapter adapter = new RemindersAdapter(this, reminders);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(mDividerItemDecoration);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_reminder_button);
        floatingActionButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), AddReminderActivity.class);
                startActivity(i);
            }
        });

        RemindMeService.start(this);
    }
}
