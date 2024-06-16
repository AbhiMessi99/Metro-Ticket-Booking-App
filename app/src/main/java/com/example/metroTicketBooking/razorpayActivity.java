package com.example.metroTicketBooking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class razorpayActivity extends BaseActivity implements PaymentResultListener {

    private Handler handler = new Handler();
    private String To, From, type, cost;
    private TextView journeyType, money, source, destination, route, currentTimeTextView, passenger;
    private int tripFare;
    private ImageView razorpay, home;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SessionManager sessionManager; // Assuming this is your session manager class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_razorpay);
        Checkout.preload(getApplicationContext());

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Initialize UI components
        journeyType = findViewById(R.id.textViewJType);
        source = findViewById(R.id.textViewSource);
        destination = findViewById(R.id.textViewDestination);
        money = findViewById(R.id.textViewMoney);
        route = findViewById(R.id.textViewRoute);
        currentTimeTextView = findViewById(R.id.textViewTimeOftravel);
        passenger = findViewById(R.id.textViewPassenger);
        razorpay = findViewById(R.id.imageViewRazorpay);
        home = findViewById(R.id.imageViewRazHome);

        Intent intent = getIntent();
        money.setText("₹ " + intent.getStringExtra("money"));
        journeyType.setText(intent.getStringExtra("journeyType"));
        source.setText(intent.getStringExtra("source"));
        destination.setText(intent.getStringExtra("destination"));
        route.setText(intent.getStringExtra("route"));
        passenger.setText(intent.getStringExtra("passenger"));
        tripFare = Integer.parseInt(intent.getStringExtra("money"));
        To = intent.getStringExtra("destination");
        From = intent.getStringExtra("source");
        cost = intent.getStringExtra("money");
        type = intent.getStringExtra("journeyType");

        updateTime();

        razorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makepayment(tripFare);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Transaction Cancelled! Ticket not booked.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                if (currentTimeTextView != null) {
                    currentTimeTextView.setText(currentTime);
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void makepayment(int tripFare) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_GaxlJ9Hue5xPUo"); // Move to a secure place in production
        checkout.setImage(R.drawable.logo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "To Kolkata Metro Railway Cop.");
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", tripFare * 100); // pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact", "8789629431");
            options.put("method", "upi");

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        HashMap<String, String> userDetail = sessionManager.getUserDetail();
        String username = userDetail.get(sessionManager.KEY_USER);

        PaymentDetails paymentDetails = new PaymentDetails(
                username,
                s,
                tripFare,
                System.currentTimeMillis()
        );

        TicketDetails details = new TicketDetails(From, To, cost, type, System.currentTimeMillis());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("MetroUsers");

        databaseReference.child(username).child("payments").push().setValue(paymentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getApplicationContext(), myTicketActivity.class);
                intent.putExtra("journeyType", type);
                intent.putExtra("source", From);
                intent.putExtra("destination", To);
                intent.putExtra("fare", cost);
                startActivity(intent);
            }
        });

        databaseReference.child(username).child("Tickets").push().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Ticket Booked Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("Payment Error", s);
        Toast.makeText(getApplicationContext(), "Payment failed: " + s, Toast.LENGTH_LONG).show();
    }
}
