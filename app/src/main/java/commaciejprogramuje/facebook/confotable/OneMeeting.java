package commaciejprogramuje.facebook.confotable;

import android.support.annotation.NonNull;

/**
 * Created by m.szymczyk on 2017-11-08.
 */

public class OneMeeting implements Comparable<OneMeeting> {
    private String summary;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String reservationDate;

    public OneMeeting(String summary, String startDate, String endDate, String startTime, String endTime, String reservationDate) {
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationDate = reservationDate;
    }

    @Override
    public int compareTo(@NonNull OneMeeting oneMeeting) {
        int dateComparator = startDate.compareTo(oneMeeting.startDate);

        if (dateComparator == 0) {
            return startTime.compareTo(oneMeeting.startTime);
        } else {
            return dateComparator;
        }
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
}
