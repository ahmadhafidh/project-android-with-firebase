package com.ahmad.reminderme;

public class Activity {
    private String activity;
    private int time, DOW, id;
    private boolean enabled;

    public Activity(){

    }

    public Activity(String activity, int time, int DOW, int id, boolean enabled) {
        this.activity = activity;
        this.time = time;
        this.DOW = DOW;
        this.id = id;
        this.enabled = enabled;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDayOfWeek() {
        return DOW;
    }

    public void setDayOfWeek(int DOW) {
        this.DOW = DOW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
