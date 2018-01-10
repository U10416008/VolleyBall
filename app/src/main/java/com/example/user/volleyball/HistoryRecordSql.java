package com.example.user.volleyball;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dingjie on 2018/1/6.
 */

public class HistoryRecordSql extends SQLiteOpenHelper {
    public HistoryRecordSql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE  TABLE history " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "name VARCHAR, " +
                "name2 VARCHAR, " +
                "first VARCHAR, " +
                "second VARCHAR, " +
                "third VARCHAR, " +
                "forth VARCHAR, " +
                "fifth VARCHAR, " +
                "date VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void add(String name , String name2 ,String first , String second , String third , String forth,String fifth,String date) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("name2", name2);

        values.put("first", first);
        values.put("second", second);
        values.put("third", third);
        values.put("forth",forth);
        values.put("fifth",fifth);
        values.put("date",date);

        long id = db.insert("history", null, values);
        Log.d("ADD", id + "");


        db.close();

    }


}
