package com.example.user.volleyball;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 2017/12/5.
 */

public class RemindSql extends SQLiteOpenHelper {
    Context context;
    public RemindSql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE  TABLE alert " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "date VARCHAR, " +
                "schedule INTEGER, "+
                "min INTEGER, " +
                "hour INTEGER, " +
                "am_pm VARCHAR, " +
                "note VARCHAR)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void add(String date ,int schedule, int min,int hour , String AM_PM , String note ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String loc = "";

        ContentValues values = new ContentValues();
        values.put("schedule",schedule);
        values.put("date", date);
        values.put("min", min);
        values.put("hour", hour);
        values.put("am_pm", AM_PM);
        values.put("note", note);
        long id = db.insert("alert", null, values);
        Log.d("ADD", id + "");


        db.close();

    }
    public boolean update(String date ,int schedule, int min,int hour , String AM_PM , String note){
        String selectQuery = "SELECT * FROM " + "alert";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ContentValues values = new ContentValues();
        values.put("schedule",schedule);
        values.put("min", min);
        values.put("hour", hour);
        values.put("am_pm", AM_PM);
        values.put("note", note);

        db.update("alert", values, "date = ?", new String[]{date});

        Log.d("UPDATE", note);
        return true;
    }
    public boolean contain(String date){
        String selectQuery = "SELECT * FROM " + "alert";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {
                if(date.equals(cursor.getString(1))){
                    return true;
                }

            } while (cursor.moveToNext());
        }
// return contact list

        return false;
    }
    public boolean delete(String date , int schedule){
        if(contain(date)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("alert", "date = ? and schedule = ?", new String[]{date , String.valueOf(schedule)});
            return true;
        }else{
            return false;
        }

    }
}

