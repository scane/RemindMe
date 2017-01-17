package com.scanba.remindme;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.scanba.remindme.models.Reminder;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{Reminder.class};

    public static void main(String[] args) throws IOException, SQLException {

        writeConfigFile("ormlite_config.txt", classes);

    }
}
