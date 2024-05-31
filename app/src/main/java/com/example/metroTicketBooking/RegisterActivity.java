package com.example.metroTicketBooking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9]([@#._](?![@#_.-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    public static final String PHONE_PATTERN = "^[0-9]{10}$";
    public static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);
    public static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
    EditText uid;
    EditText pw, pw2;
    EditText email;
    EditText number;
    EditText editTextDate;
    Calendar cal;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView textviewToLogin;
    Button btn;
    RadioGroup rdg;
    RadioButton rb;
    DBhelperClass DB;
    private CountryCodePicker countryCodePicker;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        uid = (EditText) findViewById(R.id.editTextRegisterUsername);
        pw = (EditText)findViewById(R.id.editTextRegisterPassword);
        pw2 = (EditText)findViewById(R.id.editTextRegisterRePassword);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("MetroUsers");
        email = (EditText)findViewById(R.id.editTextTextEmailAddress);
        number = (EditText)findViewById(R.id.editTextPhone2);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDate.setShowSoftInputOnFocus(false);
        cal = Calendar.getInstance();
        textviewToLogin = findViewById(R.id.textviewToLogin);
        btn = (Button) findViewById(R.id.registerButton);
        rdg = findViewById(R.id.radioGroupGender);
        DB = new DBhelperClass(this);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        countryCodePicker.registerCarrierNumberEditText(number);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH,day);
                updateDate();
            }
        };

        uid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(pattern.matcher(uid.getText().toString()).matches()==false){
                    uid.setError("1. Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.\n" +
                            "2. Special characters allowed are (#), (@) and (_) .\n" +
                            "3. Special characters must not be the first or last character.\n" +
                            "4. Special characters must not appear consecutively,\n" +
                            "5. The number of characters must be between 5 to 20.");
                    btn.setEnabled(false);
                } else{
                    btn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() == false){
                    email.setError("Invalid Email input!");
                    btn.setEnabled(false);
                }else btn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(phonePattern.matcher(number.getText().toString()).matches()==false) {
                    number.setError("Phone number must contain 10 digits");
                    btn.setEnabled(false);
                }else btn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = uid.getText().toString();
                String pass = pw.getText().toString();
                String pass2 = pw2.getText().toString();
                String emailid =email.getText().toString();
                String num = countryCodePicker.getFullNumberWithPlus();
                String dob = editTextDate.getText().toString();
                int genderselected = rdg.getCheckedRadioButtonId();
                rb = findViewById(genderselected);
                String gender = rb.getText().toString();
                if(user.toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"Username is empty", Toast.LENGTH_SHORT).show();
                }else if (pass.length()<9) {
                    Toast.makeText(RegisterActivity.this,"Password should be atleast 9 characters", Toast.LENGTH_SHORT).show();
                }else if (emailid.equals("")) {
                    Toast.makeText(RegisterActivity.this,"Email is empty", Toast.LENGTH_SHORT).show();
                }else if(Patterns.EMAIL_ADDRESS.matcher(emailid).matches() == false){
                    Toast.makeText(RegisterActivity.this,"Invalid email input", Toast.LENGTH_SHORT).show();
                }else if (num.toString().equals("")) {
                    Toast.makeText(RegisterActivity.this,"Number is empty", Toast.LENGTH_SHORT).show();
                }else if (dob.toString().equals("")) {
                    Toast.makeText(RegisterActivity.this,"Date is empty", Toast.LENGTH_SHORT).show();
                }else if (pass.equals(pass2)==false) {
                    Toast.makeText(RegisterActivity.this,"Passwords doesnot match", Toast.LENGTH_SHORT).show();
                } else{
                    if (DB.checkValidity(user,emailid,num) == 0){
                        Toast.makeText(getApplicationContext(), "Please Verify your mobile number", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), phoneLogin.class);
                        intent.putExtra("username", user);
                        intent.putExtra("password", pass);
                        intent.putExtra("emailid", emailid);
                        intent.putExtra("number", num);
                        intent.putExtra("dob", dob);
                        intent.putExtra("gender", gender);
                        startActivity(intent);
                    }else{
                        int val = DB.checkValidity(user,emailid,num);
                        if( val == 1)
                            Toast.makeText(getApplicationContext(),"Username not available", Toast.LENGTH_SHORT).show();
                        else if(val == 2)
                            Toast.makeText(getApplicationContext(),"User with this email is already registered", Toast.LENGTH_SHORT).show();
                        else if(val == 3)
                            Toast.makeText(getApplicationContext(),"User with this mobile is already registered", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textviewToLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        editTextDate.setOnClickListener(view -> {
            new DatePickerDialog(RegisterActivity.this,date,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void updateDate() {
        String df = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(df, Locale.US);
        editTextDate.setText(sdf.format(cal.getTime()));
    }
}