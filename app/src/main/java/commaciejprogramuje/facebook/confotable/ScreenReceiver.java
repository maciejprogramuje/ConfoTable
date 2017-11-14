package commaciejprogramuje.facebook.confotable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by m.szymczyk on 2017-11-14.
 */

public class ScreenReceiver extends BroadcastReceiver {
    // THANKS JASON
    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // DO WHATEVER YOU NEED TO DO HERE

            wasScreenOn = false;

            PowerManager newPM = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = newPM.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "TEST");
            wakeLock.acquire();

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent ExistingIntent = intent;
            PendingIntent pi = PendingIntent.getActivity(context, 0, ExistingIntent, 0);
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10, pi);

            Log.w("UWAGA", "screen OFF");
            wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // AND DO WHATEVER YOU NEED TO DO HERE
            Log.w("UWAGA", "screen ON");
            wasScreenOn = true;
        }
    }
}
