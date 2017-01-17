package com.scanba.remindme.models;

import android.accounts.Account;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.Date;

@DatabaseTable(tableName = "reminders")
public class Reminder {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String description;

    @DatabaseField
    private Date remindAt;

    public Reminder() {

    }

    public Reminder(String description, Date remindAt) {
        this.description = description;
        this.remindAt = remindAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getRemindAt() {
        return remindAt;
    }

    public void delete(Dao<Reminder, Integer> reminderDao) {
        DeleteBuilder<Reminder, Integer> deleteBuilder = reminderDao.deleteBuilder();
        try {
            deleteBuilder.where().eq("id", getId());
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
