package com.karim.com.io.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karim.com.io.MainActivity;
import com.karim.com.io.Model.User;
import com.karim.com.io.R;
import com.karim.com.io.shared;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    View registerLayout,loginLayout;
    TextView nameText,phoneNumberText,postionText,passwordText,loginPassword,loginPhoneNumber;
    ProgressBar pb;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference mReference=database.getReference("User");
    SharedPreferences sharedPreferences;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallsBack;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcalls;
    private FirebaseAuth mAuth;

    public void Close(View view){
        finish();
    }
    private void __init__(){
        sharedPreferences=getSharedPreferences("pref",MODE_PRIVATE);
        registerLayout=findViewById(R.id.register_layout);
        loginLayout=findViewById(R.id.login_layout);
        nameText=findViewById(R.id.nameText);
        phoneNumberText=findViewById(R.id.phoneText);
        postionText=findViewById(R.id.postionText);
        passwordText=findViewById(R.id.passordText);
        pb=findViewById(R.id.pb);
        loginPassword=findViewById(R.id.loginPassword);
        loginPhoneNumber=findViewById(R.id.loginPhone);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w=getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_register);
        __init__();
    }
    public void viewRegisterClicked(View v){
        loginLayout.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
    }
    public void viewLoginClicked(View v){
        loginLayout.setVisibility(View.VISIBLE);
        registerLayout.setVisibility(View.GONE);
    }
    public void Registerfunc(View v){
        pb.setVisibility(View.VISIBLE);
        nameText.setEnabled(false);
        passwordText.setEnabled(false);
        phoneNumberText.setEnabled(false);
        postionText.setEnabled(false);
        validation();
    }
    private void validation(){
        if(nameText.getText()==null){
            nameText.setError("please enter your name");
        }else if(phoneNumberText.getText()==null){
            phoneNumberText.setText("please enter your phone number");
        }else if(postionText.getText()==null){
            postionText.setError("please enter your position");
        }else if(passwordText.getText()==null){
            postionText.setError("please enter your password");
        }else{
            shared.phoneNumber=phoneNumberText.getText().toString();
            pushData();
        }
    }
    private void pushData(){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(phoneNumberText.getText().toString())){
                    Toast.makeText(Register.this, "this number has beeen registred before", Toast.LENGTH_SHORT).show();
                }else{
                    User user=new User();
                    user.setName(nameText.getText().toString());
                    user.setPassword(passwordText.getText().toString());
                    user.setPhoneNumner(phoneNumberText.getText().toString());
                    user.setPosition(postionText.getText().toString());
                    mReference.child(user.getPhoneNumner()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                       finish();
                       startActivity(new Intent(Register.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("error",e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void login(View v){
        pb.setVisibility(View.VISIBLE);
        loginPhoneNumber.setEnabled(false);
        loginPassword.setEnabled(false);
        mcalls=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                // ...
            }

        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                loginPhoneNumber.getText().toString(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Register.this,               // Activity (for callback binding
                mcalls );



        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(loginPhoneNumber.getText().toString())){
                    mReference.child(loginPhoneNumber.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user=dataSnapshot.getValue(User.class);
                            if(user.getPassword().equals(loginPassword.getText().toString())){
                                shared.phoneNumber=  loginPhoneNumber.getText().toString();
                                finish();
                                startActivity(new Intent(Register.this,MainActivity.class));
                            }else{
                                Toast.makeText(Register.this, "invaid input", Toast.LENGTH_SHORT).show();
                                loginPhoneNumber.setEnabled(true);
                                loginPassword.setEnabled(true);
                                pb.setVisibility(View.GONE);
                            }
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
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            pb.setVisibility(View.GONE);
//                            String s = loginPhoneNumber.getText().toString();
//                            FirebaseUser user = task.getResult().getUser();
//                            startActivity(new Intent(Register.this, MainActivity.class));
//                            shared.phoneNumber = s;
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                                Toast.makeText(getBaseContext(), task.getException().getMessage() + " some thing worng", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
    }


}
