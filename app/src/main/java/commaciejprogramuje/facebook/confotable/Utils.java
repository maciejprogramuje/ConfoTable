package commaciejprogramuje.facebook.confotable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.provider.Settings;
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

    public static void disableStatusBar(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 23) {
            WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    // this is to enable the notification to recieve touch events
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    // Draws over status bar
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            localLayoutParams.height = (int) (50 * context.getResources().getDisplayMetrics().scaledDensity);
            localLayoutParams.format = PixelFormat.TRANSPARENT;
            CustomViewGroup view = new CustomViewGroup(context);
            assert manager != null;
            manager.addView(view, localLayoutParams);
        }


    }
}
