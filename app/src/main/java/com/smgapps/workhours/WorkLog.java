package com.smgapps.workhours;

public class WorkLog {
    private int id;
    private String date;
    private String hours;
    private String minutes;
    private String startTime;
    private String endTime;

    public WorkLog() {
    }

    public WorkLog(String dateTime, String hours, String minutes) {
        this.date = dateTime;
        this.hours = hours;
        this.minutes = minutes;
    }

    public WorkLog(String dateTime, String hours, String minutes, String startTime, String endTime) {
        this.date = dateTime;
        this.hours = hours;
        this.minutes = minutes;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public WorkLog(int id, String dateTime, String hours, String minutes) {
        this.id = id;
        this.date = dateTime;
        this.hours = hours;
        this.minutes = minutes;
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

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
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
