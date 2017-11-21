package commaciejprogramuje.facebook.confotable;

import android.os.AsyncTask;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.PropertyList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static commaciejprogramuje.facebook.confotable.MainActivity.inputFileUrl;

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
        //https://github.com/ical4j/ical4j/wiki/Examples
        Log.w("UWAGA", "parsuję...");

        ArrayList<OneMeeting> tempArr = new ArrayList<>();

        Calendar today = Calendar.getInstance();
        Calendar monthAfterToday = Calendar.getInstance();
        monthAfterToday.add(Calendar.MONTH, 1);

        Period period = null;
        net.fortuna.ical4j.model.Calendar cal = null;
        String tSummary = "";
        String tStartDate = "";
        String tEndDate = "";
        try {
            InputStream is = new URL(inputFileUrl).openStream();
            cal = new CalendarBuilder().build(is);
            period = new Period(new DateTime(today.getTime()), new DateTime(monthAfterToday.getTime()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }

        for (Object o : cal.getComponents("VEVENT")) {
            Component c = (Component)o;
            PeriodList list = c.calculateRecurrenceSet(period);

            for (Object po : list) {
                PropertyList properties = c.getProperties();
                tSummary = properties.getProperty("SUMMARY").getValue();
                tStartDate = ((Period)po).getStart().toString();
                tEndDate = ((Period)po).getEnd().toString();

                //Log.w("UWAGA", "sum: "+tSummary+", s: "+tStartDate+", e: "+tEndDate);
                tempArr.add(new OneMeeting(tSummary, tStartDate, tEndDate, ""));
            }
        }

        Log.w("UWAGA", "koniec parsowania");

        Collections.sort(tempArr);
        return tempArr;
    }

    @Override
    protected void onPostExecute(ArrayList<OneMeeting> oneMeetings) {
        listener.onTaskCompletedListener(oneMeetings);
    }

    public interface OnTaskCompletedListener {
        void onTaskCompletedListener(ArrayList<OneMeeting> arrayList);
    }
}