package com.example.metroTicketBooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class forgetPasswordActivity extends AppCompatActivity {

    Button validate, changePassword;
    DBhelperClass DB;
    EditText mobEmail;
    EditText newPassword, reNewPassword;
    ImageView back;
    TextView knowUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        mobEmail = findViewById(R.id.editTextFPhoneEmail);
        newPassword = findViewById(R.id.editTextFNewPassword);
        reNewPassword = findViewById(R.id.editTextFReNewPassword);
        validate = findViewById(R.id.ValidateButton);
        back = findViewById(R.id.imageViewBacktoLogin);
        changePassword = findViewById(R.id.buttonUpdatePassword);
        knowUsername = findViewById(R.id.textViewKnowUsername);
        DB = new DBhelperClass(this);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DB.checkForForgetting(mobEmail.getText().toString())){
                    newPassword.setVisibility(View.VISIBLE);
                    reNewPassword.setVisibility(View.VISIBLE);
                    validate.setVisibility(view.GONE);
                    mobEmail.setFocusable(false);
                    changePassword.setVisibility(view.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(), "No user registered with above email or mobile number", Toast.LENGTH_LONG).show();
                }
            }
        });
        knowUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String credential = mobEmail.getText().toString();
                String username;
                if(credential.isEmpty()){
                    Toast.makeText(forgetPasswordActivity.this, "Provide Phone number or Email", Toast.LENGTH_SHORT).show();
                }else{
                    username = DB.getUsername(credential);
                    if(username.isEmpty()){
                        Toast.makeText(forgetPasswordActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(forgetPasswordActivity.this, "Your Username: "+username, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String credential = mobEmail.getText().toString();
                String password = newPassword.getText().toString();
                String rePassword = reNewPassword.getText().toString();
                if(password.length()>=8) {
                    if (password.equals(rePassword)) {
                        int check = DB.forgetPassword(password, credential);
                        if (check > 0) {
                            Toast.makeText(getApplicationContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(forgetPasswordActivity.this, "Unexpected error occured Try after some Time", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(forgetPasswordActivity.this, "Password and re entered password cannot be same", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(forgetPasswordActivity.this, "Password must be of 8 characters Minimum", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}