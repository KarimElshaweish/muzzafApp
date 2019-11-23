package com.karim.com.io;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.karim.com.io.Activites.Durations;
import com.karim.com.io.Activites.newoffer;
import com.karim.com.io.Activites.timerList;
import com.karim.com.io.Model.PlaceData;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity implements LocationListener {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.wins:

                startActivity(new Intent(this, Durations.class));
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    TextView result, durationTex;
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    ImageView reloadImage;
    BottomAppBar bottomAppBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mReference = database.getReference("Timer");
    DatabaseReference mTimerReference = database.getReference("Time");
    View timerLayout,layoutfailer;
    TextView finLocation;

    private void __init__() {
        offerTxt=findViewById(R.id.new_offer);
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Offer")){
                    offerTxt.setVisibility(View.VISIBLE);
                    offerTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this,newoffer.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        checkGPS();
        layoutfailer=findViewById(R.id.layoutfailer);
        durationTex = findViewById(R.id.duration);
        finLocation=findViewById(R.id.findLocation);
        mTimerReference.child("Time").child("hours").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                durationTex.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        result = findViewById(R.id.result);
        surfaceView = findViewById(R.id.camerapreivew);
        reloadImage = findViewById(R.id.img_reload);
        bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);
        timerLayout = findViewById(R.id.layoutTimer);
        todayTimerChecking();
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceView.getVisibility() == View.VISIBLE)
                    Toast.makeText(MainActivity.this, "please scan the code first", Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(MainActivity.this, timerList.class));
            }
        });

    }

    boolean find = false;

    private void todayTimerChecking() {
        find = false;
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(shared.phoneNumber)) {
                    mReference.child(shared.phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ");
                            String stDate;
                            Date date = new Date();
                            stDate = dateFormat.format(date);
                            String[] arrlist = stDate.split("/");
                            stDate = arrlist[0] + "-" + arrlist[1] + "-" + arrlist[2];
                            if (dataSnapshot.hasChild(stDate)) {
                               timerLayout.setVisibility(View.VISIBLE);
                            } else
                                surfaceView.setVisibility(View.VISIBLE);

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    timerLayout.setVisibility(View.GONE);
                    surfaceView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    LocationManager mLocationManager;
    Geocoder gecoder;
    Double longtuide,lontuide;
    List<Address>list;
    @Override
    public void onLocationChanged(Location location) {
        if(placeData!=null) {
            longtuide = location.getLongitude();
            lontuide = location.getLatitude();

            Location loc1 = new Location("");
            loc1.setLatitude( Double.parseDouble(placeData.getLituide()));
            loc1.setLongitude(Double.parseDouble(placeData.getLongtuide()));

            Location loc2 = new Location("");
            loc2.setLatitude(lontuide);
            loc2.setLongitude(longtuide);

            float distanceInMeters = loc1.distanceTo(loc2);
            gecoder = new Geocoder(getBaseContext(), Locale.getDefault());
            finLocation.setText(String.valueOf(distanceInMeters));
            if(distanceInMeters>20000){

                surfaceView.setVisibility(View.GONE);
                layoutfailer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
PlaceData placeData;
    private void checkGPS() {
        DatabaseReference mPlaceReference = database.getReference("Location");
        mPlaceReference.child("Place").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placeData = dataSnapshot.getValue(PlaceData.class);
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                Location location1 = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
                onLocationChanged(location1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    TextView offerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w=getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        __init__();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode>qrCodes=detections.getDetectedItems();
                if(qrCodes.size()!=0){
                    result.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            result.setText(qrCodes.valueAt(0).displayValue);
                            cameraSource.stop();
                            startActivity(new Intent(MainActivity.this,timerList.class));
                        }
                    });
                }
            }
        });

       // surfaceView.setVisibility(View.GONE);
    }




}
