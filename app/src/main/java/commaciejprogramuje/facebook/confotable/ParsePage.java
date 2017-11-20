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
        Calendar actualCalendar = Calendar.getInstance();

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

                    int tYear = Integer.valueOf(tEndDate.substring(0, 4));
                    int tMonth = Integer.valueOf(tEndDate.substring(4, 6));
                    int tDay = Integer.valueOf(tEndDate.substring(6, 8));

                    if (tYear >= actualCalendar.get(Calendar.YEAR)
                            && tMonth >= (actualCalendar.get(Calendar.MONTH) + 1)
                            && tDay >= actualCalendar.get(Calendar.DAY_OF_MONTH)) {
                        tempArr.add(new OneMeeting(tSummary, tStartDate, tEndDate, ""));
                        Collections.sort(tempArr);
                    }

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


        /*
        ArrayList<OneMeeting> tempArr = new ArrayList<>();
        Calendar actualCalendar = Calendar.getInstance();

        try {
            BufferedReader br = new BufferedReader(new FileReader(resultFileName));
            String line;
            String tempSummary = "";
            String[] tempDtStart = new String[3];
            String tempDtStartToCal = "";
            String[] tempDtEnd = new String[3];
            String tempDtStamp = "";
            String tempTimeStart = "";
            String tempTimeEnd = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("SUMMARY")) {
                    tempSummary = line.replace("SUMMARY:", "");
                } else if (line.contains("DTSTART")) {
                    tempDtStartToCal = line.substring(line.indexOf(":") + 1, line.indexOf(":") + 9);
                    tempDtStart = Utils.splitDate(tempDtStartToCal);
                    if (line.length() > 10) {
                        tempTimeStart = line.substring(line.indexOf(":") + 9).replace("T", "");
                        tempTimeStart = Utils.splitTime(tempTimeStart);
                    } else {
                        tempTimeStart = "00:00";
                    }
                } else if (line.contains("DTEND")) {
                    if (line.length() > 10) {
                        tempDtEnd = Utils.splitDate(line.substring(line.indexOf(":") + 1, line.indexOf(":") + 9));
                        tempTimeEnd = line.substring(line.indexOf(":") + 9).replace("T", "");
                        tempTimeEnd = Utils.splitTime(tempTimeEnd);
                    } else {
                        tempDtEnd = Utils.splitDate(line.substring(line.indexOf(":") + 1));
                        tempTimeEnd = "23:59";
                    }
                } else if (line.contains("DTSTAMP")) {
                    tempDtStamp = line.substring(line.indexOf(":") + 1);
                } else if (line.contains("END:VEVENT")) {
                    if (Integer.valueOf(tempDtEnd[0]) >= actualCalendar.get(Calendar.YEAR)
                            && Integer.valueOf(tempDtEnd[1]) >= (actualCalendar.get(Calendar.MONTH) + 1)
                            && Integer.valueOf(tempDtEnd[2]) >= actualCalendar.get(Calendar.DAY_OF_MONTH)) {
                        Log.w("UWAGA", "----------------------- OK! " + line);
                        tempArr.add(new OneMeeting(tempSummary, tempDtStart, tempDtEnd, tempTimeStart, tempTimeEnd, tempDtStamp));
                        Collections.sort(tempArr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempArr;*/
    }

    @Override
    protected void onPostExecute(ArrayList<OneMeeting> oneMeetings) {
        listener.onTaskCompletedListener(oneMeetings);
    }

    public interface OnTaskCompletedListener {
        void onTaskCompletedListener(ArrayList<OneMeeting> arrayList);
    }
}