package commaciejprogramuje.facebook.confotable;

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

    public static String splitDate(String tempDate) {
        if(tempDate.length() > 0) {
            String y = tempDate.substring(0, 4);
            String m = tempDate.substring(4, 6);
            String d = tempDate.substring(6, 8);
            return d + "/" + m + "/" + y;
        }
        return  "";
    }

    public static String splitTime(String tempTime) {
        if(tempTime.length() > 0) {
            String h = tempTime.substring(0, 2);
            String mm = tempTime.substring(2, 4);
            return h + ":" + mm;
        }
        return "";
    }
}
