package com.karim.com.io.DataBase;

public class Data {
   private String Date,St_Time,End_Time;

    public Data(String date, String st_Time, String end_Time) {
        Date = date;
        St_Time = st_Time;
        End_Time = end_Time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSt_Time() {
        return St_Time;
    }

    public void setSt_Time(String st_Time) {
        St_Time = st_Time;
    }

    public String getEnd_Time() {
        return End_Time;
    }

    public void setEnd_Time(String end_Time) {
        End_Time = end_Time;
    }
}
