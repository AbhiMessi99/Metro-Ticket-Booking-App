package com.example.metroTicketBooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class BaseActivity extends AppCompatActivity {

    private static final long TIMEOUT = 600000; // 10 seconds in milliseconds
    private Handler handler = new Handler();
    private Runnable runnable;
    SessionManager sessionManager;
    GoogleSignInAccount acc;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
        Intent intend = getIntent();
        acc = GoogleSignIn.getLastSignedInAccount(this);
        startTimer();
    }

    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                redirectToLogin();
            }
        };
        handler.postDelayed(runnable, TIMEOUT);
    }

    private void redirectToLogin() {
        Toast.makeText(getApplicationContext(), "Session Timed Out Please Login again!!", Toast.LENGTH_SHORT).show();
        if(acc!=null){
            signoutGoogle();
            sessionManager.logoutUser();
        }
        else{
            sessionManager.logoutUser();
        }
        finishAffinity(); // Close all activities in the task stack
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the runnable when the activity is destroyed to prevent memory leaks
        handler.removeCallbacks(runnable);
    }

    private void signoutGoogle() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
            }
        });
    }
}
