package commaciejprogramuje.facebook.confotable;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.WindowManager;

class Utils {
    static String[] splitDate(String tempDate) {
        String[] tempArr = new String[3];
        if (tempDate.length() > 0) {
            tempArr[0] = tempDate.substring(0, 4);
            tempArr[1] = tempDate.substring(4, 6);
            tempArr[2] = tempDate.substring(6, 8);
        }
        return tempArr;
    }

    static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, FakeLauncherActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    static void setScreenFullBright(Activity activity) {
        //setBrightness(activity, FULL_BRIGHT_LEVEL);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // turn off sleep mode
    }

    static void setScreenHalfBright(Activity activity) {
        //setBrightness(activity, HALF_BRIGHT_LEVEL);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // turn on sleep mode
    }

    private static void setBrightness(Activity activity, int brightness) {
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }
}
