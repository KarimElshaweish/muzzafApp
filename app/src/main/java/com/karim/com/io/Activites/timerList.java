package com.karim.com.io.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karim.com.io.Brodcast.MyReceiver;

import com.karim.com.io.Model.TimerData;
import com.karim.com.io.R;

import com.karim.com.io.shared;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class timerList extends AppCompatActivity {
    private static long START_TIME_IN_MILLIS;    //8000;//28800000
    TextView dateText,countDownText;
    private  CountDownTimer countDownTimer;
    private long mTimeLeftInMillis;
    boolean timerRunning=false;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference mReference=database.getReference("Timer");
    DatabaseReference mTimeRef=database.getReference("Time");
    String stDate;
    Long staticEndTime;
    TimerData stTimer;
    ProgressBar pb;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_list);

        dateText=findViewById(R.id.date);
        mTimeRef.child("Time").child("hours").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                START_TIME_IN_MILLIS=Long.parseLong(dataSnapshot.getValue().toString())*60*60*1000;
                mTimeLeftInMillis=START_TIME_IN_MILLIS;
                Long when=System.currentTimeMillis()+START_TIME_IN_MILLIS;
                AlarmManager am= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent=new Intent(timerList.this,MyReceiver.class);
                intent.putExtra("myAction","notify");
                PendingIntent pendingIntent=PendingIntent.getBroadcast(timerList.this,0,intent,0);
                am.set(AlarmManager.RTC_WAKEUP,when,pendingIntent);
                DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd ");
                final Date date=new Date();
                stDate=dateFormat.format(date);
                String[]arrlist=stDate.split("/");
                stDate=arrlist[0]+"-"+arrlist[1]+"-"+arrlist[2];
                dateText.setText(stDate);
                pb=findViewById(R.id.pb);
                countDownText=findViewById(R.id.conuntDownText);
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String phoneNumber= shared.phoneNumber;
                        if(dataSnapshot.hasChild(phoneNumber)){
                            mReference.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(stDate)){
                                        TimerData td=dataSnapshot.child(stDate).getValue(TimerData.class);
                                        stTimer=td;
                                        if(td.getEndTime()-System.currentTimeMillis()<=0){
                                            td.setRunning(false);
                                            String timeFormated1=String.format(Locale.getDefault(), "%02d:%02d:%02d", 00, 00, 00);
                                            countDownText.setText(timeFormated1);
                                            pb.setVisibility(View.GONE);
                                            mReference.child(phoneNumber).setValue(td);
                                        }
                                        StartTimer(td.getEndTime());
                                    }else{
                                        Long stTime=System.currentTimeMillis();
                                        final Long EndTime=stTime+START_TIME_IN_MILLIS;
                                        TimerData timerData=new TimerData();

                                        timerData.setPhoneNumber(phoneNumber);
                                        timerData.setEndTime(EndTime);
                                        timerData.setDate(stDate);
                                        timerData.setStartTime(stTime);
                                        timerData.setRunning(true);
                                        stTimer=timerData;
                                        mReference.child(phoneNumber).child(stDate).setValue(timerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                StartTimer(EndTime);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(timerList.this, "some thing wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            final Long EndTime=System.currentTimeMillis()+START_TIME_IN_MILLIS;
                            final TimerData timerData=new TimerData();

                            timerData.setPhoneNumber(phoneNumber);
                            timerData.setEndTime(EndTime);
                            timerData.setDate(stDate);
                            timerData.setRunning(true);
                            timerData.setStartTime(System.currentTimeMillis());
                            stTimer=timerData;
                            mReference.setValue(shared.phoneNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mReference.child(shared.phoneNumber).setValue(timerData.getDate()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mReference.child(shared.phoneNumber).setValue(timerData.getDate()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mReference.child(shared.phoneNumber).child(timerData.getDate()).setValue(timerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            StartTimer(EndTime);

                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    });
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    private void StartTimer(Long end){
        staticEndTime=end;
        mTimeLeftInMillis=end-System.currentTimeMillis();
        if(mTimeLeftInMillis<=0) {
            mTimeLeftInMillis = 0;

        }
        countDownTimer=new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                updateCountDownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerRunning=false;
            }
        }.start();
    }
    private void updateCountDownText(Long time){

        if(time<=0){
            stTimer.setRunning(false);
            mReference.child(stTimer.getPhoneNumber()).setValue(stTimer);
            return;
        }
        String timeFormated;
        if(stTimer.isRunning()&& stTimer != null) {
            int minutes = (int) (time / 1000) % 60;
            int seconds = (int) (time / (1000 * 60)) % 60;
            int hours = (int) ((time / (1000 * 60 * 60)) % 24);
             timeFormated = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, seconds, minutes);
        }else{
            timeFormated=String.format(Locale.getDefault(), "%02d:%02d:%02d", 00, 00, 00);
            pb.setVisibility(View.GONE);
        }
        countDownText.setText(timeFormated);


    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
}
