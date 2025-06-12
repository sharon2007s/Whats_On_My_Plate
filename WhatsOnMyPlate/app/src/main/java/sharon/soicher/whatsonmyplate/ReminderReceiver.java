package sharon.soicher.whatsonmyplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // When triggered, send the walking reminder notification.
        StepsTrackerActivity.sendWalkingReminder(context);
    }
}