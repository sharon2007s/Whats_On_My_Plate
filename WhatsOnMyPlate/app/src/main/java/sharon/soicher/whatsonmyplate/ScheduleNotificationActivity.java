package sharon.soicher.whatsonmyplate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleNotificationActivity extends AppCompatActivity {

    private EditText etTitle, etLocation, etStartTime, etEndTime, etDate;
    private Button btnScheduleNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_notification);

        // Initialize the views
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etDate = findViewById(R.id.etDate);
        btnScheduleNotification = findViewById(R.id.btnScheduleNotification);

        btnScheduleNotification.setOnClickListener(v -> scheduleNotification());
    }

    private void scheduleNotification() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Expecting date in "yyyy-MM-dd" and startTime in "HH:mm" format
        String dateTimeString = date + " " + startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        long triggerTimeMillis;
        try {
            Date dateObj = sdf.parse(dateTimeString);
            triggerTimeMillis = dateObj.getTime();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date or time format.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that the time is in the future
        if (triggerTimeMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "The scheduled time is in the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug log
        Log.d("ScheduleNotification", "Scheduling alarm for: " + triggerTimeMillis);

        // Create the intent with extras
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TITLE, title);
        intent.putExtra(AlarmReceiver.EXTRA_LOCATION, location);
        intent.putExtra(AlarmReceiver.EXTRA_START_TIME, startTime);
        intent.putExtra(AlarmReceiver.EXTRA_END_TIME, endTime);
        intent.putExtra(AlarmReceiver.EXTRA_DATE, date);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Toast.makeText(this, "Alarm Manager not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                // Open system settings where the user can allow your exact alarms
                Intent intent1 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent1);
                return; // wait until the user comes back
            }
        }

        // Use setExactAndAllowWhileIdle to schedule the alarm
        Toast.makeText(this, "About to schedule for: " + triggerTimeMillis, Toast.LENGTH_LONG).show();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
        Toast.makeText(this, "Notification scheduled successfully.", Toast.LENGTH_SHORT).show();
    }
}