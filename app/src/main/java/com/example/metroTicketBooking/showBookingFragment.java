package com.example.metroTicketBooking;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.*;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link showBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class showBookingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SessionManager sessionManager;
    String currUser;

    private ListView listView;
    private listAdapter adapter;
    private List<TicketDetails> dataList;
    ImageView qrcode;
    private ProgressBar bar;
    private DatabaseReference databaseReference;

    public showBookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment showBookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static showBookingFragment newInstance(String param1, String param2) {
        showBookingFragment fragment = new showBookingFragment();
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
        View view = inflater.inflate(R.layout.fragment_show_booking, container, false);
        sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetail= sessionManager.getUserDetail();
        currUser = userDetail.get(sessionManager.KEY_USER);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("MetroUsers").child(currUser).child("Tickets");
        listView = view.findViewById(R.id.listViewAllTickets);
        dataList = new ArrayList<>();
        bar = view.findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
        // Initialize adapter
        adapter = new listAdapter(getActivity(), dataList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous data
                dataList.clear();
                // Iterate through dataSnapshot to get data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TicketDetails data = snapshot.getValue(TicketDetails.class);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TicketDetails ticket = dataList.get(position);
                showPopupWindow(view, ticket);
            }
        });
        return view;
    }

    private void showPopupWindow(View anchorView, TicketDetails ticket) {
        // Inflate the custom layout/view
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.ticket_popup, null);
        qrcode = popupView.findViewById(R.id.imageViewQrCode);
        // Initialize a new instance of popup window
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set the details in the popup
        String Source = ticket.getSource();
        String Destination = ticket.getDestination();
        String Fare = ticket.getFare();
        String qrText = Source+ " -> "
                + Destination
                + " fare: Rs " + Fare;
        // Show the popup window
        generateQRCode(qrText, qrcode);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void generateQRCode(String text, ImageView imageView) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "QR Code generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}