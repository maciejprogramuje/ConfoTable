package commaciejprogramuje.facebook.confotable;

import android.os.AsyncTask;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static commaciejprogramuje.facebook.confotable.MainActivity.INPUT_FILE_URL;

/**
 * Created by m.szymczyk on 2017-11-08.
 */

public class ParsePage extends AsyncTask<String, Void, ArrayList<OneMeeting>> {
    public OnTaskCompletedListener listener = null;

    public ParsePage(OnTaskCompletedListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<OneMeeting> doInBackground(String... strings) {
        Log.w("UWAGA", "parsuję...");

        ArrayList<OneMeeting> tempArr = new ArrayList<>();

        InputStream is = null;
        try {
            String tSummary = "";
            String tStartDate = "";
            String tEndDate = "";

            is = new URL(INPUT_FILE_URL).openStream();
            net.fortuna.ical4j.model.Calendar cal = new CalendarBuilder().build(is);


            for (Object vevent : cal.getComponents()) {
                Component component = (Component) vevent;
                //Log.w("UWAGA", "component: " + component.getName());
                if (component.getName().equals("VEVENT")) {
                    for (Object o : component.getProperties()) {
                        Property property = (Property) o;
                        String name = property.getName();
                        String value = property.getValue();

                        //Log.w("UWAGA", "property: " + name + "=" + value);

                        if (name.equals("SUMMARY")) {
                            tSummary = value == null ? "Spotkanie" : value;
                        } else if (name.equals("DTSTART")) {
                            tStartDate = value;
                        } else if (name.equals("DTEND")) {
                            tEndDate = value;
                        }
                    }

                    if(isAfterCurrentDateAndTime(tEndDate)) {
                        tempArr.add(new OneMeeting(tSummary, tStartDate, tEndDate, ""));
                    }
                    Collections.sort(tempArr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.w("UWAGA", "koniec parsowania");

        return tempArr;
    }

    private boolean isAfterCurrentDateAndTime(String s) {
        int tYear = Integer.valueOf(s.substring(0, 4));
        int tMonth = Integer.valueOf(s.substring(4, 6));
        int tDay = Integer.valueOf(s.substring(6, 8));
        int tHour = 0;
        int tMinutes = 0;
        if (s.length() > 9) {
            tHour = Integer.valueOf(s.substring(9, 11));
            tMinutes = Integer.valueOf(s.substring(11, 13));
        }

        Calendar actualCalendar = Calendar.getInstance();

        if (tYear > actualCalendar.get(Calendar.YEAR)) {
            return true;
        } else if (tYear == actualCalendar.get(Calendar.YEAR) && tMonth > (actualCalendar.get(Calendar.MONTH) + 1)) {
            return true;
        } else if (tYear == actualCalendar.get(Calendar.YEAR) && tMonth == (actualCalendar.get(Calendar.MONTH) + 1) && tDay > actualCalendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else if (tYear == actualCalendar.get(Calendar.YEAR) && tMonth == (actualCalendar.get(Calendar.MONTH) + 1) && tDay == actualCalendar.get(Calendar.DAY_OF_MONTH) && tHour > actualCalendar.get(Calendar.HOUR_OF_DAY)) {
            return true;
        } else if (tYear == actualCalendar.get(Calendar.YEAR) && tMonth == (actualCalendar.get(Calendar.MONTH) + 1) && tDay == actualCalendar.get(Calendar.DAY_OF_MONTH) && tHour == actualCalendar.get(Calendar.HOUR_OF_DAY) && tMinutes > actualCalendar.get(Calendar.MINUTE)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(ArrayList<OneMeeting> oneMeetings) {
        listener.onTaskCompletedListener(oneMeetings);
    }

    public interface OnTaskCompletedListener {
        void onTaskCompletedListener(ArrayList<OneMeeting> arrayList);
    }
}