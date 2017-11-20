package commaciejprogramuje.facebook.confotable;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by m.szymczyk on 2017-11-08.
 */

public class OneMeeting implements Comparable<OneMeeting>, Serializable {
    private String summary;
    private String[] startDateArr = new String[3];
    private String[] endDateArr = new String[3];
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String reservationDate;
    private String onlyStartDate;
    private String onlyEndDate;
    private String onlyStartTime;
    private String onlyEndTime;

    public OneMeeting() {
        summary = "Launching...";
        startTime = "";
        endTime = "";
        reservationDate = "";
    }

    public OneMeeting(String summary, String startDate, String endDate, String reservationDate) {
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationDate = reservationDate;

        startDateArr = Utils.splitDate(startDate);
        endDateArr = Utils.splitDate(endDate);

        if (startDate.length() > 9) {
            startTime = startDate.substring(9, 13);
        } else {
            startTime = "0000";
        }
        onlyStartTime = startTime.substring(0, 2) + ":" + startTime.substring(2);

        if (endDate.length() > 9) {
            endTime = endDate.substring(9, 13);
        } else {
            endTime = "2359";
        }
        onlyEndTime = endTime.substring(0, 2) + ":" + endTime.substring(2);

        onlyStartDate = startDateArr[2] + "/" + startDateArr[1] + "/" + startDateArr[0];
        onlyEndDate = endDateArr[2] + "/" + endDateArr[1] + "/" + endDateArr[0];
    }

    @Override
    public int compareTo(@NonNull OneMeeting oneMeeting) {
        int dateYearComparator = startDateArr[0].compareTo(oneMeeting.startDateArr[0]);
        if (dateYearComparator == 0) {
            int dateMonthComparator = startDateArr[1].compareTo(oneMeeting.startDateArr[1]);
            if(dateMonthComparator == 0) {
                int dateDayComparator = startDateArr[2].compareTo(oneMeeting.startDateArr[2]);
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

    public String[] getStartDateArr() {
        return startDateArr;
    }

    public void setStartDateArr(String[] startDateArr) {
        this.startDateArr = startDateArr;
    }

    public String[] getEndDateArr() {
        return endDateArr;
    }

    public void setEndDateArr(String[] endDateArr) {
        this.endDateArr = endDateArr;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOnlyStartDate() {
        return onlyStartDate;
    }

    public void setOnlyStartDate(String onlyStartDate) {
        this.onlyStartDate = onlyStartDate;
    }

    public String getOnlyEndDate() {
        return onlyEndDate;
    }

    public void setOnlyEndDate(String onlyEndDate) {
        this.onlyEndDate = onlyEndDate;
    }

    public String getOnlyStartTime() {
        return onlyStartTime;
    }

    public void setOnlyStartTime(String onlyStartTime) {
        this.onlyStartTime = onlyStartTime;
    }

    public String getOnlyEndTime() {
        return onlyEndTime;
    }

    public void setOnlyEndTime(String onlyEndTime) {
        this.onlyEndTime = onlyEndTime;
    }
}
