package com.example.metroTicketBooking;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class loginActivity extends AppCompatActivity {

    DBhelperClass DB;
    TextView textViewToRegister ;
    EditText uid;
    EditText pw;
    TextView forget;
    Button btn;
    private SessionManager sessionManager;

    GoogleSignInClient gsc;

    ImageView googleimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
        sessionManager = new SessionManager(this);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        uid = (EditText)findViewById(R.id.editTextLoginUsername);
        pw = (EditText)findViewById(R.id.editTextLoginPassword);
        forget = findViewById(R.id.textViewForgetPassword);
        btn = findViewById(R.id.buttonProceed);
        DB = new DBhelperClass(this);
        googleimage = findViewById(R.id.googlesignin);

        textViewToRegister = findViewById(R.id.textViewSignUp);
        textViewToRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);

                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = uid.getText().toString();
                String pass = pw.getText().toString();
                if(user.equals("")){
                    uid.setError("Username is empty!");
                }else if (pass.equals("")) {
                    pw.setError("Password is empty!");
                }
                else{
                    if(DB.authenticate(user,pass)){
                        Toast.makeText(getApplicationContext(),uid.getText()+" is Logging in",Toast.LENGTH_SHORT).show();
                        String email = DB.getEmail(user);
                        sessionManager.createLoginSession(user,pass,email);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("UID", user);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /*forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, forgetPassword.class);
                startActivity(intent);
            }
        });*/

        googleimage.setOnClickListener(view -> signin());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    int RC_SIGN_IN =1000;
    private void signin() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                String email = acc.getEmail();
                sessionManager.createLoginSession(String.valueOf(email.hashCode()),"default","default");
                navigateUpToSecondActivity();

            } catch (ApiException e) {
                Log.d("TAG", "signInResult:failed code=" + e.getStatusCode(), e);
                Toast.makeText(getApplicationContext(), "Login Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void navigateUpToSecondActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
        if(sessionManager.isLoggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}