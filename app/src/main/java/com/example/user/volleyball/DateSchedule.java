package com.example.user.volleyball;

/**
 * Created by dingjie on 2017/12/14.
 */

public class DateSchedule {
    private String date = "";
    private String schedule = "";
    private String note = "";
    DateSchedule(String date ,String schedule,String note){
        this.date = date;
        this.schedule = schedule;
        this.note = note;
    }
    public String getDate(){
        return date;
    }
    public String getSchedule(){
        return schedule;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setSchedule(String schedule){
        this.schedule = schedule;
    }
    public String getNote(){
        return note;
    }
    public void setNote(String note){
        this.note = note;
    }
}
