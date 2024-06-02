package com.example.metroTicketBooking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import java.util.*;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    private Handler handler = new Handler();
    private Runnable runnable;
    FloatingActionButton fab;
    GoogleSignInAccount acc;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DrawerLayout drawerLayout;
    String currUser;
    BottomNavigationView bottomNavigationView;
    TextView a;
    SessionManager sessionManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                menuItem.setChecked(true);
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView navUsername = header.findViewById(R.id.textViewNavUsername);
        TextView navEmail = header.findViewById(R.id.textViewNavEmail);
        ImageView navPic = header.findViewById(R.id.imageViewProfile);
        Intent intent = getIntent();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
        acc = GoogleSignIn.getLastSignedInAccount(this);
        HashMap<String, String> userDetail= sessionManager.getUserDetail();
        currUser = userDetail.get(sessionManager.KEY_USER);
        if(acc!=null){
            navUsername.setText(acc.getDisplayName());
            navEmail.setText(acc.getEmail());
            Uri personPhoto = acc.getPhotoUrl();
            if (personPhoto != null) {
                Glide.with(this)
                        .load(personPhoto)
                        .transform(new CircularTransformation())
                        .into(navPic);
            }
        }
        else {
            navUsername.setText(userDetail.get(sessionManager.KEY_USER));
            navEmail.setText(userDetail.get(sessionManager.KEY_EMAIL));
        }
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.nav_logout)
            {
                if(acc!=null) {
                    sessionManager.logoutUser();
                    signoutGoogle();
                }
                else{
                    sessionManager.logoutUser();
                    finish();
                }
            } else if (menuItem.getItemId() == R.id.nav_tDetails) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("MetroUsers").child(currUser).child("payments");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            transactionFragment destFragment = new transactionFragment();
                            replaceFragment(destFragment);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Transactions exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            } else if (menuItem.getItemId() == R.id.nav_home){
                HomeFragment destFragment = new HomeFragment();
                replaceFragment(destFragment);
            } else if (menuItem.getItemId() == R.id.nav_MyProfile) {
                myProfileFragment destFragment = new myProfileFragment();
                replaceFragment(destFragment);
            }
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            return true;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle  = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Perform cleanup of old transactions on activity start
        cleanupOldTransactionsForAllUsers(firebaseDatabase.getReference("MetroUsers"));
        cleanupOldTicketsForAllUsers(firebaseDatabase.getReference("MetroUsers"));

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        replaceFragment(new HomeFragment());
    }

    //outside onCreate
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragmentManager.isStateSaved()) {
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            // If the state is already saved, use commitAllowingStateLoss() as a last resort
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void cleanupOldTicketsForAllUsers(DatabaseReference databaseReference) {
        long fourHoursAgo = System.currentTimeMillis() - 8 * 60 * 60 * 1000;

        // Access the root of the database to retrieve all users
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate through each user in the database
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Access the Tickets child node of the current user
                    userSnapshot.child("Tickets").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ticketsSnapshot) {
                            // Iterate through each transaction in the Tickets node
                            for (DataSnapshot ticketSnapshot : ticketsSnapshot.getChildren()) {
                                // Deserialize the snapshot into a PaymentDetails object
                                PaymentDetails paymentDetails = ticketSnapshot.getValue(PaymentDetails.class);
                                if (paymentDetails != null && paymentDetails.timestamp < fourHoursAgo) {
                                    // Remove the transaction if it is older than four hours
                                    ticketSnapshot.getRef().removeValue()
                                            .addOnCompleteListener(task -> {
                                                // Provide feedback based on the success of the removal
                                                if (task.isSuccessful()) {
                                                    // Optional: Log success for debugging
                                                } else {
                                                    // Optional: Log failure for debugging
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database errors
                            Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cleanupOldTransactionsForAllUsers(DatabaseReference databaseReference) {
        long fourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000;

        // Access the root of the database to retrieve all users
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate through each user in the database
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Access the Tickets child node of the current user
                    userSnapshot.child("payments").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ticketsSnapshot) {
                            // Iterate through each transaction in the Tickets node
                            for (DataSnapshot ticketSnapshot : ticketsSnapshot.getChildren()) {
                                // Deserialize the snapshot into a PaymentDetails object
                                PaymentDetails paymentDetails = ticketSnapshot.getValue(PaymentDetails.class);
                                if (paymentDetails != null && paymentDetails.timestamp < fourHoursAgo) {
                                    // Remove the transaction if it is older than four hours
                                    ticketSnapshot.getRef().removeValue()
                                            .addOnCompleteListener(task -> {
                                                // Provide feedback based on the success of the removal
                                                if (task.isSuccessful()) {
                                                    // Optional: Log success for debugging
                                                } else {
                                                    // Optional: Log failure for debugging
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database errors
                            Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
