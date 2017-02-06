# RemindMe

RemindMe is a simple android app which helps you in reminding day to day tasks. Add a reminder and it will send a notification 10 minutes before the actual task. It is as that simple.

Following components/libraries are used to build this app

- Sqlite database to store reminders
- Ormlite to save and retrieve records
- A background service which checks for upcoming reminders and sends a notification if any.
- AlarmManager which executes the service at specific intervals
- RecyclerView to list reminders
- DatePickerDialog
- TimePickerDiaglog
