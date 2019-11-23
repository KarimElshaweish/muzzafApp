package com.karim.com.io.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.karim.com.io.Model.Offer;
import com.karim.com.io.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class newoffer extends AppCompatActivity {

    TextView offerName,offferDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newoffer);
        offerName=findViewById(R.id.offerName);
        offferDetails=findViewById(R.id.offferDetails);
        getdata();
    }

    private void getdata() {
        FirebaseDatabase.getInstance().getReference("Offer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Offer offer = dataSnapshot.getValue(Offer.class);
                    offerName.setText(offer.getName());
                    offferDetails.setText(offer.getDescripton());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void finish(View view) {
        finish();
    }
}
