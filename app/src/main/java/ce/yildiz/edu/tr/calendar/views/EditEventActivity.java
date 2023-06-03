package ce.yildiz.edu.tr.calendar.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.adapters.NotificationAdapter;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.models.Notification;
import ce.yildiz.edu.tr.calendar.other.ServiceAutoLauncher;
import petrov.kristiyan.colorpicker.ColorPicker;

public class EditEventActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextInputLayout eventTitleTextInputLayout; //check
    private Switch allDayEventSwitch;
    private LinearLayout setDateLinearLayout;
    private TextView setDateTextView;
    private LinearLayout setTimeLinearLayout;
    private TextView setTimeTextView;
    private RecyclerView notificationsRecyclerView;
    private TextView addNotificationTextView;
    private TextView repeatTextView;
    private TextInputLayout eventNoteTextInputLayout;
    private TextView pickNoteColorTextView;
    private AlertDialog notificationAlertDialog;
    private AlertDialog repetitionAlertDialog;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;
    private int notColor;
    private DBHelper dbHelper;
    private List<Notification> currentNotifications;
    private List<Notification> eventNotifications;
    private int oldEventId;
    private NotificationAdapter notificationAdapter;
    private Event mEvent;
    String priority;
    String type;
    //EVENT TYPE

    RadioGroup radioGroup;
    RadioButton radioButton;
    String clickedTypeName;
    Spinner spinnerType;
    String Type;

    SeekBar seekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(getAppTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dbHelper = new DBHelper(this);

        defineViews();

        initViews();
        initVariables();
        createAlertDialogs();
        defineListeners();


        radioGroup = findViewById(R.id.radioGroup);
        //textView = findViewById(R.id.text_view_selected);

        Button buttonApply = findViewById(R.id.button_apply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(radioId);

                //textView.setText("Your choice: " + radioButton.getText());
            }
        });
        setSupportActionBar(toolbar);

//==========================SEEKBAR==========================
        seekBar = findViewById(R.id.seekbar);
        //textView = findViewById(R.id.textView2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i){
                    case 0:
                        //textView.setText("Low");
                        priority="Low";
                        Toast.makeText(EditEventActivity.this,"Low", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        //textView.setText("Medium");
                        priority="Medium";
                        Toast.makeText(EditEventActivity.this,"Medium", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        //textView.setText("High");
                        priority="High";
                        Toast.makeText(EditEventActivity.this,"High", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        Toast.makeText(this, "Selected Radio Button: " + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
        type= (String) radioButton.getText() ;
    }

    private void defineViews() {
        eventTitleTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_EventTitle);
        allDayEventSwitch = (Switch) findViewById(R.id.AddNewEventActivity_Switch_AllDayEvent);
        setDateLinearLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_LinearLayout_SetDate);
        setDateTextView = (TextView) findViewById(R.id.AddNewEventActivity_TexView_SetDate);
        setTimeLinearLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_LinearLayout_SetTime);
        setTimeTextView = (TextView) findViewById(R.id.AddNewEventActivity_TexView_SetTime);
        notificationsRecyclerView = (RecyclerView) findViewById(R.id.AddNewEventActivity_RecyclerView_Notifications);
        repeatTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_Repeat);
        addNotificationTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_Add_Notification);
        eventNoteTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_Note);
        pickNoteColorTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_PickNoteColor);
        progressBar = (ProgressBar) findViewById(R.id.AddNewEventActivity_ProgressBar);
        toolbar = (Toolbar) findViewById(R.id.AddNewEventActivity_Toolbar);

    }

    @SuppressLint("ResourceType")
    private void initViews() {

        Intent intent = getIntent();
        int eventId = intent.getIntExtra("eventId", 0);
        mEvent = readEvent(eventId);
        oldEventId = mEvent.getId();

        eventTitleTextInputLayout.getEditText().setText(mEvent.getTitle());


        seekBar = findViewById(R.id.seekbar);

        if(mEvent.getPriority()==null)
            mEvent.setPriority("Low");

        switch (mEvent.getPriority()) {
            case "Low":
                seekBar.setProgress(0);
                break;
            case "Medium":
                seekBar.setProgress(1);
                break;
            case "High":
                seekBar.setProgress(2);  //Toast.makeText(this, "hi",Toast.LENGTH_LONG).show();
                break;
            default:
                seekBar.setProgress(0);
                break;
        }

        switch (mEvent.getType()) {
            case "Travel":
                radioButton = findViewById(R.id.radio_one);
                radioButton.setChecked(true);
                break;
            case "Meeting":
                radioButton = findViewById(R.id.radio_two);
                radioButton.setChecked(true);
                break;
            case "Salary":
                radioButton = findViewById(R.id.radio_three);
                radioButton.setChecked(true);
                break;
            case "Others":
                radioButton = findViewById(R.id.radio_four);
                radioButton.setChecked(true);
                break;
        }


        setDateTextView.setText(intent.getStringExtra("eventDate"));

        if (mEvent.isAllDay()) {
            allDayEventSwitch.setChecked(true);
            setTimeLinearLayout.setVisibility(View.GONE);

        } else {
            allDayEventSwitch.setChecked(false);
            setTimeTextView.setText(mEvent.getTime());
        }

//        eventNotifications = readNotifications(mEvent.getId());
//        cancelAlarms(eventNotifications);
        currentNotifications = new ArrayList<>(readNotifications(mEvent.getId()));
        setUpRecyclerView();

        repeatTextView.setText(mEvent.getRecurringPeriod());

        eventNoteTextInputLayout.getEditText().setText(mEvent.getNote());

        GradientDrawable bgShape = (GradientDrawable) pickNoteColorTextView.getBackground();
        bgShape.setColor( mEvent.getColor());


    }

    private void initVariables() {
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeZone(TimeZone.getDefault());
        try {
            mCal.setTime(Utils.eventDateFormat.parse(setDateTextView.getText().toString()));
            alarmYear = mCal.get(Calendar.YEAR);
            alarmMonth = mCal.get(Calendar.MONTH);
            alarmDay = mCal.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void createAlertDialogs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        // Notification AlertDialog
        final View notificationDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_notification, null, false);
        RadioGroup notificationRadioGroup = (RadioGroup) notificationDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        notificationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentNotifications.add(new Notification(((RadioButton) notificationDialogView.findViewById(checkedId)).getText().toString()));
                notificationAlertDialog.dismiss();
                setUpRecyclerView();
            }
        });
        builder.setView(notificationDialogView);
        notificationAlertDialog = builder.create();
        ((Button) notificationDialogView.findViewById(R.id.AlertDialogLayout_Button_Back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAlertDialog.dismiss();
            }
        });

        // Event repetition AlertDialog
        final View eventRepetitionDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_repeat, null, false);
        RadioGroup eventRepetitionRadioGroup = (RadioGroup) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        eventRepetitionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                repeatTextView.setText("Repeat " + ((RadioButton) eventRepetitionDialogView.findViewById(checkedId)).getText().toString());
                repetitionAlertDialog.dismiss();
            }
        });
        builder.setView(eventRepetitionDialogView);
        repetitionAlertDialog = builder.create();
        ((Button) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_Button_Back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repetitionAlertDialog.dismiss();
            }
        });

    }

    private void defineListeners() {
        allDayEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setTimeLinearLayout.setVisibility(View.GONE);
                } else {
                    setTimeLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        setDateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

        setTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(view);
            }
        });


        addNotificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationAlertDialog.show();
            }
        });

        repeatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetitionAlertDialog.show();
            }
        });

        pickNoteColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNoteColor(view);
            }
        });


    }

    private Event readEvent(int eventId) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = dbHelper.readEvent(sqLiteDatabase, eventId);
        event.setRecurringPeriod(dbHelper.readRecurringPeriod(sqLiteDatabase, event.getId()));
        sqLiteDatabase.close();
        return event;
    }

    private ArrayList<Notification> readNotifications(int eventId) {
        ArrayList<Notification> notifications = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventNotifications(sqLiteDatabase, eventId);
        while (cursor.moveToNext()) {
            Notification notification = new Notification();
            notification.setId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_ID)));
            notification.setEventId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_EVENT_ID)));
            notification.setTime(cursor.getString(cursor.getColumnIndex(DBTables.NOTIFICATION_TIME)));
            notification.setChannelId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_CHANNEL_ID)));
            notifications.add(notification);
        }
        sqLiteDatabase.close();
        return notifications;
    }


    public void setTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aCal = Calendar.getInstance();
                aCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                aCal.set(Calendar.MINUTE, minute);
                aCal.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                String eventTime = simpleDateFormat.format(aCal.getTime());

                alarmHour = hourOfDay;
                alarmMinute = minute;

                setTimeTextView.setText(eventTime);

            }
        }, hour, minute, false);
        timePickerDialog.show();

    }

    public void setDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar aCal = Calendar.getInstance();
                aCal.set(Calendar.YEAR, year);
                aCal.set(Calendar.MONTH, month);
                aCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                aCal.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                String eventTime = simpleDateFormat.format(aCal.getTime());

                alarmYear = year;
                alarmMonth = month;
                alarmDay = dayOfMonth;

                setDateTextView.setText(eventTime);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public void pickNoteColor(View view) {
        final ArrayList<String> colors = Utils.getColors(this);
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker
                .setColors(colors)
                .setColumns(5)
                .setDefaultColorButton(R.color.blue)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        notColor = color;
                        GradientDrawable bgShape = (GradientDrawable) pickNoteColorTextView.getBackground();
                        bgShape.setColor(color);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }

    private void setUpRecyclerView() {
        notificationsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setMeasurementCacheEnabled(false);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(this, currentNotifications);
        notificationsRecyclerView.setAdapter(notificationAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ToolBar_Item_Save:
                if (confirmInputs()) {
                    if (mEvent.isRecurring()) {

                        new AlertDialog.Builder(this)
                                .setTitle("Editing a Recurring Event")
                                .setMessage("Are you sure you want to edit this recurring event? All occurrences of this event will also be edited.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getViewValues();
                                        new UpdateAsyncTask().execute();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.ic_warning)
                                .show();
                    } else {
                        getViewValues();
                        new UpdateAsyncTask().execute();
                    }
                }
                break;
        }

        return true;
    }

    private void cancelAlarms(List<Notification> notifications) {
        for (Notification notification : notifications) {
            cancelAlarm(notification.getId());
            dbHelper.deleteNotificationById(dbHelper.getWritableDatabase(), notification.getId());
        }
    }

    private void cancelAlarm(int requestCode) {
        Log.d(TAG, "cancelAlarm: " + requestCode);
        Intent intent = new Intent(getApplicationContext(), ServiceAutoLauncher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        pendingIntent.cancel();
    }

    private void setAlarms(ArrayList<Notification> notifications) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay);

        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);

        for (Notification notification : notifications) {
            Calendar aCal = (Calendar) calendar.clone();
            String notificationPreference = notification.getTime();

            if (notificationPreference.equals(getString(R.string._10_minutes_before))) {
                aCal.add(Calendar.MINUTE, -10);
            } else if (notificationPreference.equals(getString(R.string._1_hour_before))) {
                aCal.add(Calendar.HOUR_OF_DAY, -1);
            } else if (notificationPreference.equals(getString(R.string._1_day_before))) {
                aCal.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                // At the time of the event
            }

            setAlarm(notification, aCal.getTimeInMillis());
        }
    }

    private void setAlarm(Notification notification, long triggerAtMillis) {
        Log.d(TAG, "setAlarm: " + notification.getId());
        Intent intent = new Intent(this, ServiceAutoLauncher.class);
        intent.putExtra("eventTitle", mEvent.getTitle());
        intent.putExtra("eventNote", mEvent.getNote());
        intent.putExtra("eventColor", mEvent.getColor());
        intent.putExtra("eventTimeStamp", mEvent.getDate() + ", " + mEvent.getTime());
        intent.putExtra("interval", getInterval());
        intent.putExtra("soundName", getString("ringtone"));
        String asd = getInterval();
        intent.putExtra("notificationId", notification.getChannelId());

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notification.getId(), intent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    private String getInterval() {
        String interval = getString(R.string.one_time);
        String repeatingPeriod = repeatTextView.getText().toString();
        if (repeatingPeriod.equals(getString(R.string.daily))) {
            interval = getString(R.string.daily);
        } else if (repeatingPeriod.equals(getString(R.string.weekly))) {
            interval = getString(R.string.weekly);
        } else if (repeatingPeriod.equals(getString(R.string.monthly))) {
            interval = getString(R.string.monthly);
        } else if (repeatingPeriod.equals(getString(R.string.yearly))) {
            interval = getString(R.string.yearly);
        }
        return interval;
    }

    @SuppressLint("ResourceType")
    private void getViewValues() {
        Date aDate = null;
        try {
            aDate = Utils.eventDateFormat.parse((String) setDateTextView.getText());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "An error has occurred while parsing the date string");
        }
        mEvent.setTitle(eventTitleTextInputLayout.getEditText().getText().toString().trim());
        mEvent.setAllDay(allDayEventSwitch.isChecked());
        mEvent.setDate(Utils.eventDateFormat.format(aDate));
        mEvent.setMonth(Utils.monthFormat.format(aDate));
        mEvent.setYear(Utils.yearFormat.format(aDate));
        mEvent.setTime(setTimeTextView.getText().toString());
        mEvent.setNotify(!notificationAdapter.getNotifications().isEmpty());
        mEvent.setRecurring(isRecurring(repeatTextView.getText().toString()));
        mEvent.setRecurringPeriod(repeatTextView.getText().toString());
        mEvent.setNote(eventNoteTextInputLayout.getEditText().getText().toString().trim());
        //mEvent.type= ;
       if(priority !=null)
        mEvent.setPriority(priority);
       if(type !=null)
            mEvent.setType(type);

        if (notColor == 0) {
            notColor = getResources().getInteger(R.color.red);
        } else {
            mEvent.setColor(notColor);
        }

    }

    private boolean isRecurring(String toString) {
        return !toString.equals(getResources().getString(R.string.one_time));
    }

    private boolean confirmInputs() {
        if (!validateEventTitle()) {
            return false;
        }

        if (!validateNotifications()) {
            Snackbar.make(addNotificationTextView, "You cannot set a notification to the past.", BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateEventTitle() {
        String eventTitleString = eventTitleTextInputLayout.getEditText().getText().toString().trim();
        if (eventTitleString.isEmpty()) {
            eventTitleTextInputLayout.setError("Field can't be empty!");
            return false;
        } else {
            eventTitleTextInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateNotifications() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay);

        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);

        for (Notification notification : notificationAdapter.getNotifications()) {
            Calendar aCal = (Calendar) calendar.clone();
            String notificationPreference = notification.getTime();

            if (notificationPreference.equals(getString(R.string._10_minutes_before))) {
                aCal.add(Calendar.MINUTE, -10);
            } else if (notificationPreference.equals(getString(R.string._1_hour_before))) {
                aCal.add(Calendar.HOUR_OF_DAY, -1);
            } else if (notificationPreference.equals(getString(R.string._1_day_before))) {
                aCal.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                // At the time of the event
            }

            if (aCal.before(Calendar.getInstance())) {
                return false;
            }
        }
        return true;
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cancelAlarms(readNotifications(mEvent.getId()));

            dbHelper.updateEvent(dbHelper.getWritableDatabase(), oldEventId, mEvent);
            for (Notification notification : notificationAdapter.getNotifications()) {
                notification.setEventId(mEvent.getId());
                dbHelper.saveNotification(dbHelper.getWritableDatabase(), notification);
            }
            if (mEvent.isNotify()) {
                setAlarms(readNotifications(mEvent.getId()));
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dbHelper.close();
            setResult(RESULT_OK);
            finish();
        }
    }

    private int getAppTheme() {
        switch (getString("theme")) {
            case "Dark":
                return R.style.DarkTheme;
            case "Indigo":
                return R.style.DarkIndigoTheme;
        }

        return R.style.DarkIndigoTheme;
    }

    private String getString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(key, "Indigo");
    }

}