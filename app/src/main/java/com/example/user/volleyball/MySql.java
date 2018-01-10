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

public class MySql extends SQLiteOpenHelper {
    final int LOCATION_FREE = 0;
    final int LOCATION_WEAPON = 1;
    final int LOCATION_MIDDLE = 2;
    final int LOCATION_RASE = 3;
    Context context;
    public MySql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create table if no such table exists
        sqLiteDatabase.execSQL("CREATE  TABLE main.person " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "name VARCHAR, " +
                "team VARCHAR, " +
                "location VARCHAR, " +
                "height REAL, " +
                "miss REAL, " +
                "locationint REAL, " +
                "total REAL)");

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    //add teamer info
    public void add(String name , String team,int location , double height , double miss ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String loc = "";

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("team", team);
        switch (location){
            case LOCATION_FREE:
                loc = context.getString(R.string.player_location_free);
                break;
            case LOCATION_WEAPON:
                loc = context.getString(R.string.player_location_weapon);
                break;
            case LOCATION_MIDDLE:
                loc = context.getString(R.string.player_location_middle);
                break;
            case LOCATION_RASE:
                loc = context.getString(R.string.player_location_rase);
                break;

            default:
                break;
        }
        values.put("location", loc);
        values.put("height", height);
        values.put("miss", miss);
        values.put("locationint",location);
        long id = db.insert("main.person", null, values);
        Log.d("ADD", id + "");


        db.close();

    }
    //update the getRate
    public void addTotal(String name , String team,int miss,int total){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM main.person where name = ? and team = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{name,team});
        ContentValues values = new ContentValues();
        cursor.moveToFirst();
        double miss_rate = cursor.getDouble(5);
        int or_total = cursor.getInt(7);
        if(total != 0) {
            int miss_total = (int)(or_total * miss_rate/100);

            miss_total +=  miss;
            or_total += total ;
            miss_rate = (double)miss_total/(double)or_total * 100.0;
            values.put("total", 0);
            values.put("miss",0);
            db.update("main.person", values, "team = ? and name = ? ", new String[]{team ,name});
            Log.d("miss",miss_rate+"");
        }
        db.close();
    }
    public boolean update(String name , String team,int location , double height , double miss){
        String selectQuery = "SELECT * FROM " + "main.person";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("team", team);
        String loc= "";
        switch (location){
            case LOCATION_FREE:
                loc = context.getString(R.string.player_location_free);
                break;
            case LOCATION_WEAPON:
                loc = context.getString(R.string.player_location_weapon);
                break;
            case LOCATION_MIDDLE:
                loc = context.getString(R.string.player_location_middle);
                break;
            case LOCATION_RASE:
                loc = context.getString(R.string.player_location_rase);
                break;

            default:
                break;
        }
        values.put("location", loc);
        values.put("locationint", location);
        values.put("height", height);
        values.put("miss", miss);

        db.update("main.person", values, "team = ? and name = ? ", new String[]{team ,name});

        db.close();

        return true;
    }
    //search teamer
    public boolean contain(String name){
        String selectQuery = "SELECT * FROM " + "main.person";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {
                if(name.equals(cursor.getString(1))){
                    return true;
                }

            } while (cursor.moveToNext());
        }
// return contact list

        return false;
    }
    //delete teamer
    public boolean delete(String name){
        if(contain(name)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("main.person", "name = ?", new String[]{name});
            return true;
        }else{
            return false;
        }

    }
}

