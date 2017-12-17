package com.example.user.volleyball;

import android.content.Context;

/**
 * Created by dingjie on 2017/12/14.
 */

public class DateSchedule {
    private String date = "";
    private int schedule = 0;
    private String note = "";
    private int min = 0;
    private int hour = 0;
    private String AM_PM;
    private Context context;
    DateSchedule(Context context , String date , int schedule, String note, int hour , int min , String AM_PM){
        this.date = date;
        this.schedule = schedule;
        this.note = note;
        this.hour = hour;
        this.min = min;
        this.AM_PM = AM_PM;
        this.context = context;
    }
    public String getDate(){
        return date;
    }
    public int getMin(){ return min;}
    public int getHour(){ return hour;}
    public int get24Hour(){
        if(AM_PM.equals("AM")){
            return hour;
        }else{
            return hour +12;
        }
    }
    public String getScheduleString(){
        switch (schedule) {
            case R.id.radioRace:
                return context.getString(R.string.race);

            case R.id.radioPratice:
                return context.getString(R.string.pratice);

            case R.id.radioOther:
                return context.getString(R.string.other);

            default:
                return null;

        }
    }
    public int getSchedule(){
        return schedule;
    }
    public String getAM_PM(){
        return AM_PM;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setMin(int min){
        this.min = min;
    }
    public void setHour(int hour){
        this.hour = hour;
    }
    public void setSchedule(int schedule){
        this.schedule = schedule;
    }
    public void setAM_PM(String AM_PM){
        this.AM_PM = AM_PM;
    }
    public String getNote(){
        return note;
    }
    public void setNote(String note){
        this.note = note;
    }
}
