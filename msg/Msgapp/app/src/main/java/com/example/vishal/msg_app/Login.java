package com.example.vishal.msg_app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class Login extends Activity {

    private EditText number,pincode;
    private Button sendcode,verify,resend;
    private TextView textforotp;
    private String phoneverifycationid;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verifycationcallbacks;
    private PhoneAuthProvider.ForceResendingToken resendtoken;
    private FirebaseAuth fbAuth;
    private FirebaseUser user;
    private DatabaseReference firebaseDatabase,fortoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        number = (EditText)findViewById(R.id.number);
        pincode = (EditText)findViewById(R.id.pincode);

        sendcode = (Button)findViewById(R.id.sendcode);
        verify = (Button)findViewById(R.id.verify);
        resend = (Button)findViewById(R.id.resend);

        textforotp = (TextView)findViewById(R.id.textforotp);

        pincode.setVisibility(View.GONE);
        verify.setVisibility(View.GONE);
        textforotp.setVisibility(View.GONE);
        resend.setVisibility(View.GONE);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Contact");
        fortoken = FirebaseDatabase.getInstance().getReference("fortocken");
        fbAuth = FirebaseAuth.getInstance();

        if (fbAuth.getCurrentUser()!= null){
            Toast.makeText(this,fbAuth.getCurrentUser().getUid().toString(),Toast.LENGTH_SHORT).show();
        }

        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneno = number.getText().toString();
                if (phoneno.isEmpty()){
                    number.setError("please enter number");
                    return;
                }
                if (phoneno.length()<10){
                    number.setError("Enter valid number");
                    return;
                }
                pincode.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);
                textforotp.setVisibility(View.VISIBLE);
                resend.setVisibility(View.VISIBLE);

                setUpverificationcallbcks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneno,
                        60,
                        TimeUnit.SECONDS,
                        Login.this,
                        verifycationcallbacks
                );
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = pincode.getText().toString();
                if (otp.isEmpty()){
                    pincode.setError("Please enter OTP");
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneverifycationid,otp);
                signInwithPhoneAuthCredential(credential);
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneno = number.getText().toString();
                setUpverificationcallbcks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneno,
                        60,
                        TimeUnit.SECONDS,
                        Login.this,
                        verifycationcallbacks,
                        resendtoken
                );
            }
        });

    }

    private void signInwithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = authResult.getUser();

                String token = FirebaseInstanceId.getInstance().getToken();
                String user_id = user.getUid();

                fortoken.child(number.getText().toString()).setValue(new TokenUser(token,user_id));

                firebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        new AsyncTask<Void, Void, Integer>() {
                            @Override
                            protected Integer doInBackground(Void... voids) {
                                int temp = 0;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    User user = ds.getValue(User.class);
                                    Long numberindb = Long.parseLong(user.getNumber());
                                    Long num = Long.parseLong(number.getText().toString());
                                    if (numberindb.equals(num)){
                                        temp = 1;
                                    }
                                }
                                return temp;
                            }

                            @Override
                            protected void onPostExecute(Integer integer) {
                                super.onPostExecute(integer);
                                if (integer == 0){
                                    firebaseDatabase.push().setValue(new User(number.getText().toString(),fbAuth.getUid().toString()));
                                }
                            }
                        }.execute();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(Login.this,MainActivity.class);
                Login.this.finish();
                startActivity(intent);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

   private void setUpverificationcallbcks() {
        verifycationcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInwithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Login.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneverifycationid = s;
                resendtoken = forceResendingToken;
            }
        };

   }

}
