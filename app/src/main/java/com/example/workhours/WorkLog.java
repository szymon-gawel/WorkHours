package com.example.workhours;

public class WorkLog {
    private long id;
    private String date;
    private String hours;
    private String minutes;

    public WorkLog() {
    }

    public WorkLog(long id, String dateTime, String hours, String minutes) {
        this.id = id;
        this.date = dateTime;
        this.hours = hours;
        this.minutes = minutes;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getHours() {
        return hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return "WorkLog " + "dateTime = " + date + ", hours = " + hours + ", minutes = " + minutes;
    }
}
