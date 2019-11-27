package com.karim.com.io.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karim.com.io.Model.User;
import com.karim.com.io.R;

public class ForgetPassword extends AppCompatActivity {

    EditText phoneNumberText;
    TextView oldPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        phoneNumberText=findViewById(R.id.phoneNumber);
        oldPassword=findViewById(R.id.oldPassword);
    }

    public void forgetPassword(View view) {
        final String phoneNumber=phoneNumberText.getText().toString();
        if(!phoneNumber.equals("")){
            FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(phoneNumber)){
                        Toast.makeText(ForgetPassword.this, "no user with this phone number", Toast.LENGTH_SHORT).show();
                    }else{
                        FirebaseDatabase.getInstance().getReference("User").child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.getValue(User.class);
                                oldPassword.setText("the password is : "+user.getPassword());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(this, "please enter your phone number", Toast.LENGTH_SHORT).show();
        }
    }
}
