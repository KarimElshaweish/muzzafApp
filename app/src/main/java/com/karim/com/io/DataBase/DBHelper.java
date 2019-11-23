package com.karim.com.io.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TUZZEFDB="TUZZEFDB.db";
    public static final String TB_TIMER="TB_TIMER";
  //  public static final String TIMER_ID="TIMER_ID";
    public static final String TIMER_DATE="TIMER_DATE";
    public static final String TIMER_ST_TIME="TIMER_ST_TIME";
    public static final String TIMER_END_TIME="TIMER_END_TIME";
    private HashMap hp;
    public DBHelper(@Nullable Context context) {
        super(context, TUZZEFDB,null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE TB_TIMER " +
                "( TIMER_DATE TEXT,TIMER_ST_TIME LONG,TIMER_END_TIME LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS TB_TIMER");
            onCreate(db);
    }

    public boolean insertToDB(Data data) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TIMER_DATE,data.getDate());
        contentValues.put(TIMER_ST_TIME,data.getSt_Time());
        contentValues.put(TIMER_END_TIME,data.getEnd_Time());
        db.insert(TB_TIMER,null,contentValues);
        return true;
    }
    public Cursor getData(String data){

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM  TB_TIMER WHERE TIMER_DATE =" +data,null);

        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db=this.getReadableDatabase();
        int numRows=(int)DatabaseUtils.queryNumEntries(db,TB_TIMER);
        return  numRows;
    }
    public boolean update(Data data){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TIMER_ST_TIME,data.getSt_Time());
        contentValues.put(TIMER_END_TIME,data.getEnd_Time());
        contentValues.put(TIMER_DATE,data.getDate());
        db.update(TB_TIMER,contentValues,TIMER_END_TIME +" = ?",new String[]{data.getEnd_Time()});
        return true;
    }

}
