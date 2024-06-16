package com.example.metroTicketBooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class phoneLogin extends AppCompatActivity {

    private static final String TAG = "phoneLogin";
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth mAuth;
    private EditText otpInput;
    private Button signInBtn;
    private TextView resendOtpTextView;
    private String phoneNumber;
    private long timeOut = 60L;
    private ProgressBar progressBar;
    private DBhelperClass DB;
    private Intent intent;
    private String user, pass, emailid, num, gender, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        DB = new DBhelperClass(this);
        resendOtpTextView = findViewById(R.id.textViewReSendOtp);
        signInBtn = findViewById(R.id.verifyButton);
        otpInput = findViewById(R.id.editTextOtp);
        progressBar = findViewById(R.id.progressBarOtp);

        intent = getIntent();
        phoneNumber = intent.getStringExtra("number");
        user = intent.getStringExtra("username");
        pass = intent.getStringExtra("password");
        emailid = intent.getStringExtra("emailid");
        num = intent.getStringExtra("number");
        gender = intent.getStringExtra("gender");
        dob = intent.getStringExtra("dob");

        sendOtp(phoneNumber, false);

        resendOtpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp(phoneNumber, true);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredOtp = otpInput.getText().toString().trim();
                if (enteredOtp.isEmpty() || enteredOtp.length() < 6) {
                    otpInput.setError("Valid OTP is required");
                    otpInput.requestFocus();
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                    signIn(credential);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOut, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "Verification Failed", e);
                        setInProgress(false);
                        Toast.makeText(phoneLogin.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        setInProgress(false);
                        Toast.makeText(phoneLogin.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });

        if (isResend && resendingToken != null) {
            builder.setForceResendingToken(resendingToken);
        }

        mAuth.useAppLanguage();
        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    boolean runInsert = DB.insertQuery(user, pass, emailid, num, gender, dob);
                    if (runInsert) {
                        Toast.makeText(getApplicationContext(), "The user is Verified and registered, Please sign in.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "Sign-in failed", task.getException());
                    Toast.makeText(phoneLogin.this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            signInBtn.setEnabled(true);
        }
    }

    private void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOut--;
                runOnUiThread(() -> resendOtpTextView.setText("Resend OTP in " + timeOut + " seconds"));
                if (timeOut <= 0) {
                    timeOut = 60L;
                    timer.cancel();
                    runOnUiThread(() -> resendOtpTextView.setEnabled(true));
                }
            }
        }, 0, 1000);
    }
}
