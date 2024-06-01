package com.example.metroTicketBooking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link transactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class transactionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SessionManager sessionManager;
    String currUser;
    ImageView back;
    private ListView listView;
    private transactionAdapter adapter;
    private List<PaymentDetails> dataList;

    private ProgressBar bar;

    private DatabaseReference databaseReference;

    public transactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment transactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static transactionFragment newInstance(String param1, String param2) {
        transactionFragment fragment = new transactionFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        back = view.findViewById(R.id.imageViewRoutesBack);
        sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetail= sessionManager.getUserDetail();
        currUser = userDetail.get(sessionManager.KEY_USER);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("MetroUsers").child(currUser).child("payments");
        listView = view.findViewById(R.id.listViewtransectionDetail);
        dataList = new ArrayList<>();

        bar = view.findViewById(R.id.progressBarTransaction);
        bar.setVisibility(View.VISIBLE);

        // Initialize adapter
        adapter = new transactionAdapter(getActivity(), dataList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous data
                dataList.clear();
                // Iterate through dataSnapshot to get data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PaymentDetails data = snapshot.getValue(PaymentDetails.class);
                    dataList.add(data);
                }
                // Notify adapter of data changes
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(getActivity(), "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
                bar.setVisibility(View.GONE);

            }
        });
        // Set adapter to ListView
        listView.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment destFragment = new HomeFragment();
                FragmentManager fgm = getFragmentManager();
                fgm.beginTransaction()
                        .replace(R.id.main, destFragment)
                        .commit();
            }
        });
        return view;
    }
}