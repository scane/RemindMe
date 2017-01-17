package com.scanba.remindme.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.scanba.remindme.DatabaseHelper;
import com.scanba.remindme.R;
import com.scanba.remindme.models.Reminder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {
    List<Reminder> reminders;
    SimpleDateFormat dateFormat;
    Dao<Reminder, Integer> reminderDao;
    Context context;

    public RemindersAdapter(Context context, List<Reminder> data) {
        reminders = data;
        this.context = context;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            reminderDao = databaseHelper.getReminderDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.reminder_row, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.reminderDescription.setText(reminder.getDescription());
        holder.reminderDate.setText(dateFormat.format(reminder.getRemindAt()));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView reminderDescription, reminderDate;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            reminderDescription = (TextView) itemView.findViewById(R.id.reminder_description);
            reminderDate = (TextView) itemView.findViewById(R.id.reminder_date);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = item.getOrder();
            Reminder reminder = reminders.get(position);
            switch (item.getTitle().toString()) {
                case "Delete":
                    AlertDialog alertDialog = getDeleteConfirmationDialog(context, reminder, position);
                    alertDialog.show();
            }
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add(0, 0, getAdapterPosition(), "Delete");
            myActionItem.setOnMenuItemClickListener(this);
        }

        private AlertDialog getDeleteConfirmationDialog(Context context, Reminder reminder, int position)
        {
            final Reminder rem = reminder;
            final int pos = position;
            final Context ctxt = context;
            AlertDialog deleteDialogBox =new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            rem.delete(reminderDao);
                            reminders.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, reminders.size());
                            Toast.makeText(ctxt, "Reminder deleted successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            return deleteDialogBox;

        }
    }
}
