package com.example.metroTicketBooking;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    SessionManager sessionManager;
    private String mParam2;
    private ListView listView;
    ImageView bookticket;
    ImageView bookings;

    ImageView routes;
    String currUser;

    private ArrayList<String> dataList;
    private DatabaseReference databaseReference;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetail= sessionManager.getUserDetail();
        currUser = userDetail.get(sessionManager.KEY_USER);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("MetroUsers").child(currUser).child("Tickets");
        bookticket = view.findViewById(R.id.imageViewBookticket);
        routes = view.findViewById(R.id.imageViewRoutes);
        bookings = view.findViewById(R.id.imageViewMyBookings);
        bookticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingFragment destFragment = new bookingFragment();

                // Replace the current fragment with Fragment2
                FragmentManager fgm = getFragmentManager();
                fgm.beginTransaction()
                        .replace(R.id.choiceLayout, destFragment) // Replace fragment_container with the ID of the container in your activity layout
                        .addToBackStack(null) // Optional: Add the transaction to the back stack
                        .commitAllowingStateLoss();
            }
        });

        routes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            showBookingFragment destFragment = new showBookingFragment();
                            FragmentManager fgm = getFragmentManager();
                            fgm.beginTransaction()
                                    .replace(R.id.choiceLayout, destFragment)
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss();
                        } else {
                            Toast.makeText(getContext(), "Currently no booking available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            }
        });

        return view;
    }
}