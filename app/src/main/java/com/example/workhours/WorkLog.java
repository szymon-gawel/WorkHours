package com.example.workhours;

import com.google.type.DateTime;

import java.time.LocalDateTime;

public class WorkLog {
    private LocalDateTime dateTime;
    private int hours;
    private int minutes;

    public WorkLog(LocalDateTime dateTime, int hours, int minutes) {
        this.dateTime = dateTime;
        this.hours = hours;
        this.minutes = minutes;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }
}
