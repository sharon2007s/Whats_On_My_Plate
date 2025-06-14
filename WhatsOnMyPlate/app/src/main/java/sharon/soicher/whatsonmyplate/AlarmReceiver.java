package sharon.soicher.whatsonmyplate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "food_app_notifications";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_LOCATION = "extra_location";
    public static final String EXTRA_START_TIME = "extra_start_time";
    public static final String EXTRA_END_TIME = "extra_end_time";
    public static final String EXTRA_DATE = "extra_date";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract notification details from the Intent
        String title = intent.getStringExtra(EXTRA_TITLE);
        String location = intent.getStringExtra(EXTRA_LOCATION);
        String startTime = intent.getStringExtra(EXTRA_START_TIME);
        String endTime = intent.getStringExtra(EXTRA_END_TIME);
        String date = intent.getStringExtra(EXTRA_DATE);

        String contentText = "Location: " + location +
                "\nFrom: " + startTime +
                "\nTo: " + endTime +
                "\nDate: " + date;

        // Create Notification Channel if on Android O+
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Food App Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("App notifications for scheduled reminders");
            notificationManager.createNotificationChannel(channel);
        }



        // Build and display the notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notificationicon) // Ensure that this icon exists
                        .setContentTitle(title)
                        .setContentText("Tap for details")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        // Using currentTimeMillis as ID makes it unique
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}