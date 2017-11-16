package commaciejprogramuje.facebook.confotable;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by m.szymczyk on 2017-11-08.
 */

public class OneMeeting implements Comparable<OneMeeting>, Serializable {
    private String summary;
    private String[] startDate = new String[3];
    private String[] endDate = new String[3];
    private String startTime;
    private String endTime;
    private String reservationDate;

    public OneMeeting() {
        summary = "Launching...";
        startDate[0] = "";
        startDate[1] = "";
        startDate[2] = "";
        endDate[0] = "";
        endDate[1] = "";
        endDate[2] = "";
        startTime = "";
        endTime = "";
        reservationDate = "";
    }

    public OneMeeting(String summary, String[] startDate, String[] endDate, String startTime, String endTime, String reservationDate) {
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationDate = reservationDate;
    }

    @Override
    public int compareTo(@NonNull OneMeeting oneMeeting) {
        int dateYearComparator = startDate[0].compareTo(oneMeeting.startDate[0]);
        if (dateYearComparator == 0) {
            int dateMonthComparator = startDate[1].compareTo(oneMeeting.startDate[1]);
            if(dateMonthComparator == 0) {
                int dateDayComparator = startDate[2].compareTo(oneMeeting.startDate[2]);
                if(dateDayComparator == 0) {
                    return startTime.compareTo(oneMeeting.startTime);
                } else return dateDayComparator;
            } else return dateMonthComparator;
        } else return dateYearComparator;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String[] getStartDate() {
        return startDate;
    }

    public void setStartDate(String[] startDate) {
        this.startDate = startDate;
    }

    public String[] getEndDate() {
        return endDate;
    }

    public void setEndDate(String[] endDate) {
        this.endDate = endDate;
    }
}
