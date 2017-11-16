package commaciejprogramuje.facebook.confotable;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;

/**
 * Created by m.szymczyk on 2017-11-08.
 */

public class Utils {
    public static Calendar initCallendarByString(String str) {
        Calendar call = Calendar.getInstance();

        //Log.w("UWAGA", "=========== aktualna data: " + call.get(Calendar.DAY_OF_MONTH) + ", " + (call.get(Calendar.MONTH)+1) + "===========");

        int year = Integer.valueOf(str.substring(0, 4));
        int month = Integer.valueOf(str.substring(4, 6)) - 1; // bo numeracja miesięcy jest od 0
        int day = Integer.valueOf(str.substring(6, 8));
        int hour = 0;
        int min = 0;

        if (str.length() > 8) {
            hour = Integer.valueOf(str.substring(9, 11));
            min = Integer.valueOf(str.substring(11, 13));
        }
        call.set(year, month, day, hour, min);
        return call;
    }

    public static String[] splitDate(String tempDate) {
        String[] tempArr = new String[3];
        if(tempDate.length() > 0) {
            tempArr[0] = tempDate.substring(0, 4);
            tempArr[1] = tempDate.substring(4, 6);
            tempArr[2] = tempDate.substring(6, 8);
        }
        return tempArr;
    }

    public static String splitTime(String tempTime) {
        if(tempTime.length() > 0) {
            String h = tempTime.substring(0, 2);
            String mm = tempTime.substring(2, 4);
            return h + ":" + mm;
        }
        return "";
    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, FakeLauncherActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }
}
